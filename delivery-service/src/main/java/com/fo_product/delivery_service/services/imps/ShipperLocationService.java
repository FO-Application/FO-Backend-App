package com.fo_product.delivery_service.services.imps;

import com.fo_product.delivery_service.services.interfaces.IShipperLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShipperLocationService implements IShipperLocationService {
    //Key naỳ giống tên bảng trong DB
    private static final String SHIPPER_GEO_KEY = "online_shippers";
    private static final String HEARTBEAT_KEY_PREFIX = "shipper:heartbeat:";
    private static final long HEARTBEAT_TTL_SECONDS = 15; // Shipper mất kết nối 15s = offline

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    /*
    Shipper gửi tọa độ lên -> lưu vào redis + heartbeat
     */
    public void updateLocation(Long shipperId, double latitude, double longitude) {
        //Lưu shipper id kèm theo tọa độ
        redisTemplate.opsForGeo().add(
                SHIPPER_GEO_KEY,
                new Point(longitude, latitude), //Redis nhận long lat, ngược với google maps(lat, long)
                String.valueOf(shipperId)
        );

        // Set heartbeat key với TTL (cứ mỗi lần gửi location sẽ refresh TTL)
        String heartbeatKey = HEARTBEAT_KEY_PREFIX + shipperId;
        redisTemplate.opsForValue().set(heartbeatKey, "1", java.time.Duration.ofSeconds(HEARTBEAT_TTL_SECONDS));

        log.info("Đã cập nhật vị trí Shipper {}: [{}, {}]", shipperId, latitude, longitude);
    }

    @Override
    /**
     * Tìm shipper gần quán ăn nhất (trong bán kính radius km)
     * Lọc bỏ shipper đã mất heartbeat (tắt máy, mất mạng)
     */
    public List<Long> findNearbyShippers(double merchantLat, double merchantLng, double radiusKm) {
        Circle circle = new Circle(
                new Point(merchantLng, merchantLat),
                new Distance(radiusKm, Metrics.KILOMETERS)
        );

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .sortAscending(); //Shipper gần nhất xếp đầu

        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo().radius(SHIPPER_GEO_KEY, circle, args);

        if (results == null)
            return List.of();

        return results.getContent().stream()
                .map(geoResult -> Long.parseLong((String) geoResult.getContent().getName()))
                .filter(this::isShipperAlive) // Lọc bỏ shipper đã mất heartbeat
                .collect(Collectors.toList());
    }

    @Override
    /**
     * Khi Shipper Offline -> Xóa khỏi Redis để không bị nổ đơn
     */
    public void removeShipper(Long shipperId) {
        redisTemplate.opsForZSet().remove(SHIPPER_GEO_KEY, String.valueOf(shipperId));
        // Xóa heartbeat key
        redisTemplate.delete(HEARTBEAT_KEY_PREFIX + shipperId);
        log.info("Shipper {} đã offline. Đã xóa khỏi Redis.", shipperId);
    }

    @Override
    /**
     * Check xem shipper còn sống (heartbeat key còn tồn tại) hay không
     */
    public boolean isShipperAlive(Long shipperId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(HEARTBEAT_KEY_PREFIX + shipperId));
    }
}
