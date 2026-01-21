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
            OrderStatus newStatus = OrderStatus.valueOf(status);
            OrderStatus oldStatus = order.getOrderStatus();

            order.setOrderStatus(newStatus);
            Order savedOrder = orderRepository.save(order);

            if (newStatus == OrderStatus.PAID && oldStatus != OrderStatus.PAID) {
                log.info("Order {} thanh toán thành công. Đang bắn event Kafka...", savedOrder.getId());
                RestaurantDTO restaurant = getClientDTO.getRestaurantDTO(savedOrder.getMerchantId());

                OrderPaidEvent event = OrderPaidEvent.builder()
                        .orderId(savedOrder.getId())
                        .merchantId(savedOrder.getMerchantId())
                        .ownerId(restaurant.user().id())
                        .amount(savedOrder.getGrandTotal())
                        .paymentMethod(PaymentMethod.ZALOPAY.name())
                        .paidAt(LocalDateTime.now())
                        .build();

                kafkaProducerService.sendOrderPaidEvent(event);
            }
        } catch (Exception e) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
    }
}
