package com.fo_product.order_service.kafka;

import com.fo_product.order_service.kafka.events.ShipperAssignedEvent;
import com.fo_product.order_service.models.entities.Order;
import com.fo_product.order_service.models.enums.OrderStatus;
import com.fo_product.order_service.models.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final OrderRepository orderRepository;
    // Không cần ObjectMapper nữa vì Spring tự parse JSON sang Object

    /**
     * Nghe sự kiện từ Delivery Service: "Tôi đã tìm được tài xế cho đơn này rồi"
     */
    @KafkaListener(topics = "shipper-assigned-topic", groupId = "order-service-group")
    @Transactional
    public void handleShipperAssigned(ShipperAssignedEvent event) { // Pass thẳng Object vào đây
        log.info("Order Service nhận thông tin Shipper: {}", event);

        try {
            // 1. Tìm đơn hàng
            Order order = orderRepository.findById(event.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + event.getOrderId()));

            // 2. CẬP NHẬT SHIPPER ID
            order.setShipperId(event.getShipperId());

            // 3. Cập nhật trạng thái sang WAITING_FOR_PICKUP (Đã có xế, chờ đến lấy)
            // Lưu ý: Chỉ update nếu đơn đang ở trạng thái hợp lệ (VD: PREPARING, READY)
            // Nếu bạn muốn đơn giản thì cứ set luôn cũng được
            if (order.getOrderStatus() == OrderStatus.PREPARING || order.getOrderStatus() == OrderStatus.READY) {
                order.setOrderStatus(OrderStatus.WAITING_FOR_PICKUP);
            }

            orderRepository.save(order);

            log.info("Đã cập nhật Shipper ID {} và trạng thái WAITING_FOR_PICKUP cho đơn hàng {}",
                    event.getShipperId(), event.getOrderId());

        } catch (Exception e) {
            log.error("Lỗi khi cập nhật Shipper ID cho đơn hàng {}", event.getOrderId(), e);
            // Có thể throw exception để Kafka retry nếu lỗi DB tạm thời
        }
    }
}