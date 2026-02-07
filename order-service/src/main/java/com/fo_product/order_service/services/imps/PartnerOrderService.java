package com.fo_product.order_service.services.imps;

import com.fo_product.order_service.dtos.feigns.RestaurantDTO;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.exceptions.OrderException;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;
import com.fo_product.order_service.helpers.GetClientDTO;
import com.fo_product.order_service.kafka.KafkaProducerService;
import com.fo_product.order_service.kafka.events.*; // Import hết events
import com.fo_product.order_service.mappers.OrderMapper;
import com.fo_product.order_service.models.entities.Order;
import com.fo_product.order_service.models.entities.OrderItem;
import com.fo_product.order_service.models.enums.OrderStatus;
import com.fo_product.order_service.models.enums.PaymentMethod;
import com.fo_product.order_service.models.repositories.OrderRepository;
import com.fo_product.order_service.models.repositories.ReviewRepository;
import com.fo_product.order_service.services.interfaces.IPartnerOrderService;
import com.fo_product.order_service.dtos.responses.MerchantStatsResponse;
import com.fo_product.order_service.clients.MerchantClient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    ReviewRepository reviewRepository;
    OrderMapper mapper;
    KafkaProducerService kafkaProducerService;
    GetClientDTO getClientDTO;
    MerchantClient merchantClient;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByMerchant(Long userId, Long merchantId, String status, int page, int size) {
        boolean isUserValid = checkMerchantOwnership(userId, merchantId);
        if (!isUserValid)
            throw new OrderException(OrderErrorCode.INVALID_OWNER);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (status != null && !status.isEmpty()) {
            if (status.equals("CREATED")) {
                // Return both CREATED and PAID orders for "New Orders" tab
                java.util.List<OrderStatus> statuses = java.util.List.of(OrderStatus.CREATED, OrderStatus.PAID);
                return orderRepository.findByMerchantIdAndOrderStatusIn(merchantId, statuses, pageable)
                        .map(mapper::response);
            }
            return orderRepository.findByMerchantIdAndOrderStatus(merchantId, OrderStatus.valueOf(status), pageable)
                    .map(mapper::response);
        } else {
            return orderRepository.findByMerchantId(merchantId, pageable)
                    .map(mapper::response);
        }
    }

    // --- XÁC NHẬN ĐƠN (CONFIRM) ---
    @Override
    @Transactional
    public OrderResponse confirmAndPrepareOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        if (!checkMerchantOwnership(userId, order.getMerchantId())) {
            throw new OrderException(OrderErrorCode.INVALID_OWNER);
        }

        if (order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus() != OrderStatus.PAID) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        order.setOrderStatus(OrderStatus.PREPARING);
        Order savedOrder = orderRepository.save(order);

        log.info("Merchant confirm đơn {}. Bắn event tìm Shipper!", orderId);
        OrderConfirmedEvent event = OrderConfirmedEvent.builder()
                .orderId(order.getId())
                .merchantId(order.getMerchantId())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .productPrice(order.getSubTotal())
                .shippingFee(order.getShippingFee())
                .merchantLatitude(order.getMerchantLatitude())
                .merchantLongitude(order.getMerchantLongitude())
                .build();
        kafkaProducerService.sendOrderConfirmedEvent(event);

        return mapper.response(savedOrder);
    }

    // --- BÁO MÓN XONG (MARK READY) ---
    @Override
    @Transactional
    public OrderResponse markOrderAsReady(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        if (!checkMerchantOwnership(userId, order.getMerchantId())) {
            throw new OrderException(OrderErrorCode.INVALID_OWNER);
        }

        if (order.getOrderStatus() != OrderStatus.PREPARING) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        log.info("Merchant đã nấu xong đơn {}. Chuyển sang READY.", orderId);
        order.setOrderStatus(OrderStatus.READY);
        Order savedOrder = orderRepository.save(order);

        // [FIXED]: Lấy shipperId từ DB (đã được Consumer cập nhật trước đó)
        OrderReadyEvent event = OrderReadyEvent.builder()
                .orderId(savedOrder.getId())
                .merchantId(savedOrder.getMerchantId())
                .shipperId(savedOrder.getShipperId())
                .build();
        kafkaProducerService.sendOrderReadyEvent(event);

        return mapper.response(savedOrder);
    }

    // --- HỦY ĐƠN (CANCEL) ---
    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        if (!checkMerchantOwnership(userId, order.getMerchantId())) {
            throw new OrderException(OrderErrorCode.INVALID_OWNER);
        }

        if (order.getOrderStatus() != OrderStatus.CREATED
                && order.getOrderStatus() != OrderStatus.PAID
                && order.getOrderStatus() != OrderStatus.PREPARING) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        Order savedOrder = orderRepository.save(order);

        // [ADDED]: Bắn event Hủy đơn để hoàn tiền và báo khách
        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .orderId(savedOrder.getId())
                .merchantId(savedOrder.getMerchantId())
                .userId(savedOrder.getUserId())
                .cancelledBy("MERCHANT")
                .reason("Chủ quán hủy đơn")
                .amount(savedOrder.getGrandTotal())
                .paymentMethod(savedOrder.getPaymentMethod().name())
                .cancelledAt(java.time.LocalDateTime.now())
                .build();

        kafkaProducerService.sendOrderCancelledEvent(event);

        return mapper.response(savedOrder);
    }

    // ... (Các hàm markOrderAsDelivering, markOrderAsCompleted giữ nguyên như cũ)
    @Override
    public void markOrderAsDelivering(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        // Cho phép từ PREPARING hoặc READY chuyển sang DELIVERING
        if (order.getOrderStatus() != OrderStatus.PREPARING && order.getOrderStatus() != OrderStatus.READY) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        log.info("Shipper đã lấy đơn {}. Chuyển sang DELIVERING.", orderId);
        order.setOrderStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);

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

        if (order.getOrderStatus() != OrderStatus.DELIVERING) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        log.info("Đơn {} giao thành công. Chuyển sang COMPLETED.", orderId);
        order.setOrderStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        OrderCompletedEvent event = OrderCompletedEvent.builder()
                .orderId(order.getId())
                .merchantId(order.getMerchantId())
                .orderAmount(order.getGrandTotal())
                .build();
        kafkaProducerService.sendOrderCompletedEvent(event);
    }

    // ... Helper private method ...
    private boolean checkMerchantOwnership(Long userId, Long merchantId) {
        RestaurantDTO restaurant = getClientDTO.getRestaurantDTO(merchantId);
        if (restaurant == null) {
            log.error("Restaurant not found for merchantId: {}", merchantId);
            return false;
        }

        log.info("Checking ownership -> MerchantId: {}, UserId: {}, OwnerId: {}, UserInside: {}",
                merchantId, userId, restaurant.ownerId(), restaurant.user());

        // [FIXED] Validate using ownerId directly as UserDTO might be null
        if (restaurant.ownerId() != null) {
            boolean match = restaurant.ownerId().equals(userId);
            if (!match) log.warn("Ownership Mismatch! OwnerId: {} != UserId: {}", restaurant.ownerId(), userId);
            return match;
        }
        
        // Fallback to old check just in case ownerId is missing but UserDTO exists (legacy)
        if (restaurant.user() != null) {
            return restaurant.user().id().equals(userId);
        }
        
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));
        return mapper.response(order);
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantStatsResponse getMerchantStats(Long userId, Long merchantId) {
        if (!checkMerchantOwnership(userId, merchantId)) {
             throw new OrderException(OrderErrorCode.INVALID_OWNER);
        }

        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        long ordersToday = orderRepository.countByMerchantIdAndCreatedAtBetween(merchantId, startOfDay, endOfDay);

        BigDecimal totalRevenue = orderRepository.sumGrandTotalByMerchantIdAndStatusCompleted(merchantId);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        Double averageRating = reviewRepository.getAverageRatingByMerchantId(merchantId);
        if (averageRating == null) averageRating = 0.0;
        // Round to 1 decimal place
        averageRating = Math.round(averageRating * 10.0) / 10.0;

        // Call Merchant Service
        long menuItems = 0;
        try {
            var response = merchantClient.countProductsByRestaurant(merchantId);
             if (response != null && response.getResult() != null) {
                menuItems = response.getResult();
             }
        } catch (Exception e) {
            log.error("Error calling merchant-service for stats", e);
        }

        return MerchantStatsResponse.builder()
                .ordersToday(ordersToday)
                .totalRevenue(totalRevenue)
                .averageRating(averageRating)
                .menuItems(menuItems)
                .build();
    }
}