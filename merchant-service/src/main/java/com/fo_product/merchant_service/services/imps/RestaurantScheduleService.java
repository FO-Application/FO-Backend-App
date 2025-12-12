package com.fo_product.merchant_service.services.imps;

import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantSchedulePatchRequest;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantScheduleRequest;
import com.fo_product.merchant_service.dtos.responses.RestaurantScheduleResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;
import com.fo_product.merchant_service.mappers.RestaurantScheduleMapper;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.entities.restaurant.RestaurantSchedule;
import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantRepository;
import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantScheduleRepository;
import com.fo_product.merchant_service.services.interfaces.IRestaurantScheduleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.RESTAURANT_NOT_EXIST));

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
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.RESTAURANT_NOT_EXIST));

        RestaurantSchedule restaurantSchedule = restaurantScheduleRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.RESTAURANT_NOT_EXIST));

        if (request.dayOfWeek() != null)
            restaurantSchedule.setDayOfWeek(request.dayOfWeek());

        if (request.openTime() != null)
            restaurantSchedule.setOpenTime(request.openTime());

        if (request.closeTime() != null)
            restaurantSchedule.setCloseTime(request.closeTime());

        restaurantSchedule.setRestaurant(restaurant);

        RestaurantSchedule result = restaurantScheduleRepository.save(restaurantSchedule);

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "restaurantSchedules_details", key = "#id")
    public RestaurantScheduleResponse getRestaurantScheduleById(Long id) {
        RestaurantSchedule restaurantSchedule = restaurantScheduleRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.RESTAURANT_SCHEDULE_NOT_EXIST));

        return mapper.response(restaurantSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cacheRestaurantSchedules")
    public List<RestaurantScheduleResponse> getAllRestaurantSchedules() {
        List<RestaurantSchedule> result = restaurantScheduleRepository.findAll();

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
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.RESTAURANT_SCHEDULE_NOT_EXIST));

        restaurantScheduleRepository.delete(restaurantSchedule);
    }
}