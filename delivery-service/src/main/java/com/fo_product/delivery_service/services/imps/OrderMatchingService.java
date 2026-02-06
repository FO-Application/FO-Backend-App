package com.fo_product.delivery_service.services.imps;

import com.fo_product.delivery_service.dtos.feigns.OrderDTO;
import com.fo_product.delivery_service.helpers.GetClientDTO;
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
    private final GetClientDTO getClientDTO;
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

            OrderDTO orderRes = getClientDTO.getOrderDTO(orderEvent.orderId());
            // Gửi thông báo cho TẤT CẢ shipper tìm thấy (hoặc chỉ 1 người tùy thuật toán)
            for (Long shipperId : shipperIds) {
                // 1. Lưu Offer vào Redis (để Shipper App có thể poll)
                addPendingOffer(shipperId, orderEvent);

                // 2. Gửi Kafka Event (để bắn FCM)
                ShipperFoundEvent event = ShipperFoundEvent.builder()
                        .shipperId(shipperId)
                        .orderId(orderEvent.orderId())
                        .pickupAddress(orderRes.merchantName())
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

    // --- Helper Methods cho Pending Offers ---

    private void addPendingOffer(Long shipperId, OrderConfirmedEvent orderEvent) {
        String key = "shipper:offers:" + shipperId;
        String offerValue = gson.toJson(orderEvent); // Lưu full event để Front-end hiển thị chi tiết
        
        // Lưu vào Redis SET (hoặc List)
        redisTemplate.opsForHash().put(key, String.valueOf(orderEvent.orderId()), offerValue);
        
        // Set TTL cho key này (ví dụ 30 giây - 1 phút)
        // Lưu ý: Redis Hash TTL áp dụng cho cả key, nên nếu có nhiều offer thì có thể cần logic TTL riêng.
        // Ở đây đơn giản hóa: Refresh TTL mỗi khi có offer mới.
        redisTemplate.expire(key, java.time.Duration.ofSeconds(60));
    }

    @Override
    public List<ShipperFoundEvent> getPendingOffers(Long shipperId) {
        String key = "shipper:offers:" + shipperId;
        List<Object> offerJsons = redisTemplate.opsForHash().values(key);
        
        List<ShipperFoundEvent> results = new java.util.ArrayList<>();
        if (offerJsons == null) return results;

        for (Object json : offerJsons) {
            try {
                OrderConfirmedEvent orderEvent = gson.fromJson((String) json, OrderConfirmedEvent.class);
                
                // Cần convert sang DTO mà FE cần. Ở đây tái sử dụng ShipperFoundEvent hoặc DTO riêng.
                // Để nhanh, ta gọi lại Order Service lấy thông tin tên quán (nếu cần), hoặc dùng data lưu sẵn.
                // Lưu ý: OrderConfirmedEvent đã có lat/lon/fee. Thiếu tên quán.
                
                // Lấy lại thông tin quán từ Order Service (có thể cache)
                try {
                    OrderDTO orderRes = getClientDTO.getOrderDTO(orderEvent.orderId());
                    
                    results.add(ShipperFoundEvent.builder()
                            .shipperId(shipperId)
                            .orderId(orderEvent.orderId())
                            .pickupAddress(orderRes.merchantName()) // Tên quán
                            .lat(orderEvent.merchantLatitude())
                            .lon(orderEvent.merchantLongitude())
                            .shippingFee(orderEvent.shippingFee())
                            .build());
                } catch (Exception e) {
                    log.error("Không lấy được thông tin order {}", orderEvent.orderId());
                }

            } catch (Exception e) {
                log.error("Lỗi parse offer json", e);
            }
        }
        return results;
    }
}