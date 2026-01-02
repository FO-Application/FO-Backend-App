package com.fo_product.merchant_service.models.repositories.restaurant;

import com.fo_product.merchant_service.models.entities.restaurant.Cuisine;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    Optional<Restaurant> findBySlug(String slug);
    Page<Restaurant> findByCuisinesContaining(Cuisine cuisine, Pageable pageable);

    /**
     * Tìm nhà hàng gần nhất.
     * Công thức tính khoảng cách (6371 là bán kính trái đất tính bằng km).
     * Kết quả trả về danh sách nhà hàng, sắp xếp theo khoảng cách tăng dần.
     */
    /**
     * ST_Distance_Sphere(point(lon, lat), point(lon, lat)): Trả về khoảng cách tính bằng MÉT (meters).
     * Chúng ta nhân radiusKm * 1000 để đổi ra mét.
     */
    @Query(value = """
            SELECT * FROM restaurants r 
            WHERE r.is_active = true 
            AND ST_Distance_Sphere(point(r.longitude, r.latitude), point(:userLon, :userLat)) <= (:radiusKm * 1000)
            ORDER BY ST_Distance_Sphere(point(r.longitude, r.latitude), point(:userLon, :userLat)) ASC
            """,
            countQuery = """
            SELECT count(*) FROM restaurants r 
            WHERE r.is_active = true 
            AND ST_Distance_Sphere(point(r.longitude, r.latitude), point(:userLon, :userLat)) <= (:radiusKm * 1000)
            """,
            nativeQuery = true)
    Page<Restaurant> findNearbyRestaurants(
            @Param("userLat") Double userLat,
            @Param("userLon") Double userLon,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable
    );

    /**
     * HÀM MỚI: Tìm nhà hàng gần nhất + Lọc theo Slug danh mục
     * Logic: JOIN 3 bảng (restaurants -> restaurant_cuisine -> cuisines)
     */
    @Query(value = """
            SELECT DISTINCT r.* FROM restaurants r
            JOIN restaurant_cuisine rc ON r.id = rc.restaurant_id
            JOIN cuisines c ON rc.cuisine_id = c.id
            WHERE r.is_active = true 
            AND c.slug = :slug
            AND ST_Distance_Sphere(POINT(r.longitude, r.latitude), POINT(:userLon, :userLat)) <= (:radiusKm * 1000)
            ORDER BY ST_Distance_Sphere(POINT(r.longitude, r.latitude), POINT(:userLon, :userLat)) ASC
            """,
            countQuery = """
            SELECT count(DISTINCT r.id) FROM restaurants r
            JOIN restaurant_cuisine rc ON r.id = rc.restaurant_id
            JOIN cuisines c ON rc.cuisine_id = c.id
            WHERE r.is_active = true 
            AND c.slug = :slug
            AND ST_Distance_Sphere(POINT(r.longitude, r.latitude), POINT(:userLon, :userLat)) <= (:radiusKm * 1000)
            """,
            nativeQuery = true)
    Page<Restaurant> findNearbyRestaurantsByCuisine(
            @Param("userLat") Double userLat,
            @Param("userLon") Double userLon,
            @Param("radiusKm") Double radiusKm,
            @Param("slug") String slug,
            Pageable pageable
    );
}
