package com.fo_product.order_service.services.imps;

import com.fo_product.order_service.kafka.events.OrderPaidEvent;
import com.fo_product.order_service.dtos.feigns.RestaurantDTO;
import com.fo_product.order_service.exceptions.OrderException;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;
import com.fo_product.order_service.helpers.GetClientDTO;
import com.fo_product.order_service.kafka.KafkaProducerService;
import com.fo_product.order_service.models.entities.Order;
import com.fo_product.order_service.models.enums.OrderStatus;
import com.fo_product.order_service.models.enums.PaymentMethod;
import com.fo_product.order_service.models.repositories.OrderRepository;
import com.fo_product.order_service.services.interfaces.IInternalOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalOrderService implements IInternalOrderService {
    OrderRepository orderRepository;
    KafkaProducerService kafkaProducerService;
    GetClientDTO getClientDTO;

    @Override
    @Transactional
    public void updateAppTransId(Long orderId, String appTransId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));
        order.setAppTransId(appTransId);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOrderStatusByAppTransId(String appTransId, String status) {
        Order order = orderRepository.findByAppTransId(appTransId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_WITH_APP_TRANS_ID));

        try {
            // 1. Log để kiểm tra input đầu vào
            log.info("Đang update status cho AppTransId: {} sang Status: {}", appTransId, status);

            OrderStatus newStatus = OrderStatus.valueOf(status);
            OrderStatus oldStatus = order.getOrderStatus();

            order.setOrderStatus(newStatus);
            Order savedOrder = orderRepository.save(order);

            if (newStatus == OrderStatus.PAID && oldStatus != OrderStatus.PAID) {
                log.info("Order {} thanh toán thành công. Đang lấy thông tin Restaurant...", savedOrder.getId());

                // Khả năng cao lỗi ở đây nếu gọi sang Service khác
                RestaurantDTO restaurant = getClientDTO.getRestaurantDTO(savedOrder.getMerchantId());

                if (restaurant == null) {
                    log.error("Không tìm thấy thông tin nhà hàng cho MerchantId: {}", savedOrder.getMerchantId());
                    throw new RuntimeException("Restaurant not found");
                }

                log.info("Đã lấy xong thông tin Restaurant. Đang bắn Kafka...");

                OrderPaidEvent event = OrderPaidEvent.builder()
                        .orderId(savedOrder.getId())
                        .merchantId(savedOrder.getMerchantId())
                        .ownerId(restaurant.ownerId())
                        .amount(savedOrder.getSubTotal())
                        .paymentMethod(PaymentMethod.ZALOPAY.name())
                        .paidAt(LocalDateTime.now())
                        .build();

                kafkaProducerService.sendOrderPaidEvent(event);
                log.info("Bắn Kafka thành công!");
            }
        } catch (IllegalArgumentException e) {
            log.error("Lỗi sai tên trạng thái đơn hàng: {}", status, e);
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi update đơn hàng: ", e); // QUAN TRỌNG: Phải in stack trace ra
            throw new RuntimeException("Internal Server Error while updating order: " + e.getMessage());
        }
    }
}
