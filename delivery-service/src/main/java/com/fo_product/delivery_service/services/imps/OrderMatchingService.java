package com.fo_product.delivery_service.services.imps;

import com.fo_product.delivery_service.kafka.KafkaProducerService;
import com.fo_product.delivery_service.kafka.events.OrderConfirmedEvent;
import com.fo_product.delivery_service.kafka.events.ShipperFoundEvent;
import com.fo_product.delivery_service.services.interfaces.IOrderMatchingService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderMatchingService implements IOrderMatchingService {

    private final ShipperLocationService locationService;
    private final KafkaProducerService kafkaProducerService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Gson gson = new Gson();

    private static final String PENDING_ORDER_KEY = "pending_matching_orders";
    private static final double SEARCH_RADIUS_KM = 3.0; // Bán kính tìm
    private static final int MAX_RETRY_MINUTES = 5; // Tìm tối đa 5 phút

    /**
     * Hàm xử lý chính: Được gọi từ Kafka Consumer hoặc Scheduled Task
     */
    @Override
    public void processMatching(OrderConfirmedEvent orderEvent) {
        log.info("Đang tìm tài xế cho đơn: {}", orderEvent.orderId());

        // 1. Tìm shipper gần quán
        List<Long> shipperIds = locationService.findNearbyShippers(
                orderEvent.merchantLatitude(),
                orderEvent.merchantLongitude(),
                SEARCH_RADIUS_KM
        );

        // 2. Lọc các shipper đang bận (Logic này bạn tự thêm sau, ví dụ check trong Redis khác)
        // List<Long> availableShippers = shipperService.filterAvailable(shipperIds);

        if (!shipperIds.isEmpty()) {
            // --- TRƯỜNG HỢP A: TÌM THẤY ---
            log.info("Tìm thấy {} shipper cho đơn {}", shipperIds.size(), orderEvent.orderId());

            // Gửi thông báo cho TẤT CẢ shipper tìm thấy (hoặc chỉ 1 người tùy thuật toán)
            for (Long shipperId : shipperIds) {
                ShipperFoundEvent event = ShipperFoundEvent.builder()
                        .shipperId(shipperId)
                        .orderId(orderEvent.orderId())
                        .pickupAddress(orderEvent.deliveryAddress()) // Map tạm
                        .lat(orderEvent.merchantLatitude())
                        .lon(orderEvent.merchantLongitude())
                        .shippingFee(orderEvent.shippingFee())
                        .build();

                kafkaProducerService.sendShipperFoundEvent(event);
            }

            // Xóa khỏi danh sách chờ (nếu có)
            removeFromPendingQueue(orderEvent.orderId());

        } else {
            // --- TRƯỜNG HỢP B: KHÔNG TÌM THẤY ---
            log.warn("Chưa thấy shipper nào cho đơn {}. Đưa vào hàng đợi tìm lại.", orderEvent.orderId());
            addToPendingQueue(orderEvent);
        }
    }

    // Lưu đơn vào Redis để tìm lại sau
    @Override
    public void addToPendingQueue(OrderConfirmedEvent event) {
        // Key: PENDING_ORDER_KEY, HashKey: orderId, Value: Json Event
        // Set TTL cho key này khoảng 5-10 phút để tự hủy nếu trôi đơn
        String json = gson.toJson(event);
        redisTemplate.opsForHash().put(PENDING_ORDER_KEY, String.valueOf(event.orderId()), json);
    }

    @Override
    public void removeFromPendingQueue(Long orderId) {
        redisTemplate.opsForHash().delete(PENDING_ORDER_KEY, String.valueOf(orderId));
    }

    /**
     * CRON JOB: Chạy mỗi 15 giây để quét lại các đơn chưa có xe
     */
    @Scheduled(fixedDelay = 15000)
    public void retryFindingShippers() {
        // Lấy tất cả đơn đang treo trong Redis
        Set<Object> orderIds = redisTemplate.opsForHash().keys(PENDING_ORDER_KEY);

        if (orderIds == null || orderIds.isEmpty()) return;

        log.info("Quét lại {} đơn hàng đang chờ shipper...", orderIds.size());

        for (Object idObj : orderIds) {
            String jsonEvent = (String) redisTemplate.opsForHash().get(PENDING_ORDER_KEY, idObj);
            if (jsonEvent != null) {
                OrderConfirmedEvent event = gson.fromJson(jsonEvent, OrderConfirmedEvent.class);

                // Gọi lại hàm tìm kiếm
                processMatching(event);

                // TODO: Kiểm tra thời gian tạo đơn, nếu quá 5 phút -> Hủy đơn & Báo Merchant
            }
        }
    }
}