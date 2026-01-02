package com.fo_product.order_service.services.imps;

import com.fo_product.order_service.clients.MerchantClient;
import com.fo_product.order_service.dtos.feigns.RestaurantDTO;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.exceptions.OrderException;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;
import com.fo_product.order_service.kafka.KafkaProducerService;
import com.fo_product.order_service.kafka.events.OrderCompletedEvent;
import com.fo_product.order_service.kafka.events.OrderConfirmedEvent;
import com.fo_product.order_service.kafka.events.OrderDeliveringEvent;
import com.fo_product.order_service.mappers.OrderMapper;
import com.fo_product.order_service.models.entities.Order;
import com.fo_product.order_service.models.entities.OrderItem;
import com.fo_product.order_service.models.enums.OrderStatus;
import com.fo_product.order_service.models.repositories.OrderRepository;
import com.fo_product.order_service.services.interfaces.IPartnerOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartnerOrderService implements IPartnerOrderService {
    OrderRepository orderRepository;
    MerchantClient merchantClient;
    OrderMapper mapper;
    KafkaProducerService kafkaProducerService;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByMerchant(Long userId, Long merchantId, String status, int page, int size) {
        boolean isUserValid = checkMerchantOwnership(userId, merchantId);
        if (!isUserValid)
            throw new OrderException(OrderErrorCode.INVALID_OWNER);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Order> result;
        if (status != null && !status.isEmpty()) {
            result = orderRepository.findByMerchantIdAndOrderStatus(merchantId, OrderStatus.valueOf(status), pageable);
        } else {
            result = orderRepository.findByMerchantId(merchantId, pageable);
        }

        return result.map(mapper::response);
    }

    @Override
    @Transactional
    public OrderResponse confirmAndPrepareOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        // 1. Check quyền chủ quán
        if (!checkMerchantOwnership(userId, order.getMerchantId())) {
            throw new OrderException(OrderErrorCode.INVALID_OWNER);
        }

        // 2. Validate trạng thái: Phải là CREATED mới được xác nhận
        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        // 3. Đổi trạng thái -> PREPARING
        order.setOrderStatus(OrderStatus.PREPARING);
        Order savedOrder = orderRepository.save(order);

        // 4. Bắn Event tìm tài xế ngay lập tức
        log.info("Merchant confirm đơn {}. Bắn event tìm Shipper!", orderId);
        OrderConfirmedEvent event = OrderConfirmedEvent.builder()
                .orderId(order.getId())
                .merchantId(order.getMerchantId())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .productPrice(order.getSubTotal())
                .shippingFee(order.getShippingFee())
                .build();
        kafkaProducerService.sendOrderConfirmedEvent(event);

        return mapper.response(savedOrder);
    }

    @Override
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        if (!checkMerchantOwnership(userId, order.getMerchantId())) {
            throw new OrderException(OrderErrorCode.INVALID_OWNER);
        }

        // Chỉ đơn mới được hủy (Đang nấu dở thì tùy chính sách, ở đây cho hủy luôn)
        if (order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus() != OrderStatus.PREPARING) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        // TODO: Nếu khách trả tiền rồi thì bắn event hoàn tiền ở đây

        return mapper.response(orderRepository.save(order));
    }

    @Override
    public void markOrderAsDelivering(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        // Logic: Phải đang PREPARING (Đang nấu/Đã nấu xong) thì mới lấy hàng được
        if (order.getOrderStatus() != OrderStatus.PREPARING) {
            // Có thể thêm log warning
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        log.info("Shipper đã lấy đơn {}. Chuyển sang DELIVERING.", orderId);
        order.setOrderStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);

        // Bắn event báo khách (Notification Service sẽ nghe cái này)
        OrderDeliveringEvent event = OrderDeliveringEvent.builder()
                .orderId(order.getId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .deliveryAddress(order.getDeliveryAddress())
                .merchantName(order.getMerchantName())
                .descriptionOrder(order.getDescriptionOrder())
                .productName(order.getOrderItems().stream().map(OrderItem::getProductName).collect(Collectors.toList()))
                .productPrice(order.getSubTotal())
                .shippingFee(order.getShippingFee())
                .build();

        kafkaProducerService.sendOrderDeliveringEvent(event);
    }

    @Override
    public void markOrderAsCompleted(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        // Logic: Phải đang đi giao (DELIVERING) thì mới hoàn thành được
        if (order.getOrderStatus() != OrderStatus.DELIVERING) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        log.info("Đơn {} giao thành công. Chuyển sang COMPLETED.", orderId);
        order.setOrderStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        // Bắn event tính tiền (Revenue)
        OrderCompletedEvent event = OrderCompletedEvent.builder()
                .orderId(order.getId())
                .merchantId(order.getMerchantId())
                .orderAmount(order.getGrandTotal()) // Tổng tiền để cộng doanh thu
                .build();
        kafkaProducerService.sendOrderCompletedEvent(event);
    }

    private boolean checkMerchantOwnership(Long userId, Long merchantId) {
        RestaurantDTO restaurant = merchantClient.getRestaurant(merchantId);

        if (restaurant == null)
            return false;

        if (restaurant.user() == null || !restaurant.user().id().equals(userId))
            return false;

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        return mapper.response(order);
    }
}
