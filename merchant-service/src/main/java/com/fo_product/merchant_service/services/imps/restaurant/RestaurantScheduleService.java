package com.fo_product.merchant_service.services.imps.restaurant;

import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantSchedulePatchRequest;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantScheduleRequest;
import com.fo_product.merchant_service.dtos.responses.RestaurantScheduleResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantErrorCode;
import com.fo_product.merchant_service.mappers.RestaurantScheduleMapper;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.entities.restaurant.RestaurantSchedule;
import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantRepository;
import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantScheduleRepository;
import com.fo_product.merchant_service.services.interfaces.restaurant.IRestaurantScheduleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestaurantScheduleService implements IRestaurantScheduleService {
    RestaurantScheduleRepository restaurantScheduleRepository;
    RestaurantRepository restaurantRepository;
    RestaurantScheduleMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = "cacheRestaurantSchedules", allEntries = true)
    public RestaurantScheduleResponse createRestaurantSchedule(RestaurantScheduleRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        if (restaurantScheduleRepository.existsByRestaurantAndDayOfWeek(restaurant, request.dayOfWeek())) {
            throw new MerchantException(MerchantErrorCode.RESTAURANT_SCHEDULE_EXIST);
        }

        boolean validatedTime = validateTime(request.openTime(), request.closeTime());
        if (!validatedTime) {
            throw new MerchantException(MerchantErrorCode.SCHEDULE_TIME_NOT_VALID);
        }

        RestaurantSchedule restaurantSchedule = RestaurantSchedule.builder()
                .dayOfWeek(request.dayOfWeek())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .restaurant(restaurant)
                .build();

        RestaurantSchedule result = restaurantScheduleRepository.save(restaurantSchedule);

        return mapper.response(result);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "cacheRestaurantSchedules", allEntries = true),
                    @CacheEvict(value = "restaurantSchedules_details", key = "#id")
            }
    )
    public RestaurantScheduleResponse updateRestaurantScheduleById(Long id, RestaurantSchedulePatchRequest request) {
        RestaurantSchedule restaurantSchedule = restaurantScheduleRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_SCHEDULE_NOT_EXIST));

        if (request.dayOfWeek() != null && !request.dayOfWeek().equals(restaurantSchedule.getDayOfWeek())) {
            Restaurant restaurant = restaurantSchedule.getRestaurant();
            if (restaurantScheduleRepository.existsByRestaurantAndDayOfWeek(restaurant, request.dayOfWeek())) {
                throw new MerchantException(MerchantErrorCode.RESTAURANT_SCHEDULE_EXIST);
            }
            restaurantSchedule.setDayOfWeek(request.dayOfWeek());
        }

        LocalTime newOpenTime = request.openTime() != null ? request.openTime() : restaurantSchedule.getOpenTime();
        LocalTime newCloseTime = request.closeTime() != null ? request.closeTime() : restaurantSchedule.getCloseTime();

        boolean validatedTime = validateTime(newOpenTime, newCloseTime);
        if (!validatedTime) {
            throw new  MerchantException(MerchantErrorCode.SCHEDULE_TIME_NOT_VALID);
        }

        if (request.openTime() != null)
            restaurantSchedule.setOpenTime(request.openTime());

        if (request.closeTime() != null)
            restaurantSchedule.setCloseTime(request.closeTime());

        RestaurantSchedule result = restaurantScheduleRepository.save(restaurantSchedule);

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "restaurantSchedules_details", key = "#id")
    public RestaurantScheduleResponse getRestaurantScheduleById(Long id) {
        RestaurantSchedule restaurantSchedule = restaurantScheduleRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_SCHEDULE_NOT_EXIST));

        return mapper.response(restaurantSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cacheRestaurantSchedules", key = "#restaurantSlug")
    public List<RestaurantScheduleResponse> getAllRestaurantSchedulesByRestaurant(String restaurantSlug) {
        Restaurant restaurant = restaurantRepository.findBySlug(restaurantSlug)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        List<RestaurantSchedule> result = restaurantScheduleRepository.findAllByRestaurant(restaurant);

        return result.stream().map(mapper::response).toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "cacheRestaurantSchedules", allEntries = true),
            @CacheEvict(value = "restaurantSchedules_details", key = "#id")
    })
    public void deleteRestaurantScheduleById(Long id) {
        RestaurantSchedule restaurantSchedule = restaurantScheduleRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_SCHEDULE_NOT_EXIST));

        restaurantScheduleRepository.delete(restaurantSchedule);
    }

    private boolean validateTime(LocalTime openTime, LocalTime closeTime) {
        if  (openTime == null || closeTime == null) {
            return false;
        }
        return !openTime.isAfter(closeTime);
    }
}