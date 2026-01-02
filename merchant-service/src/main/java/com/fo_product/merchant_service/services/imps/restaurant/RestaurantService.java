package com.fo_product.merchant_service.services.imps.restaurant;

import com.fo_product.merchant_service.client.UserClient;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantPatchRequest;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantRequest;
import com.fo_product.merchant_service.dtos.responses.restaurant.RestaurantResponse;
import com.fo_product.merchant_service.dtos.feigns.UserDTO;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantErrorCode;
import com.fo_product.merchant_service.mappers.restaurant.RestaurantMapper;
import com.fo_product.merchant_service.models.entities.restaurant.Cuisine;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.repositories.restaurant.CuisineRepository;
import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantRepository;
import com.fo_product.merchant_service.services.interfaces.IMinIOService;
import com.fo_product.merchant_service.services.interfaces.restaurant.IRestaurantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestaurantService implements IRestaurantService {
    RestaurantRepository restaurantRepository;
    CuisineRepository cuisineRepository;
    IMinIOService minIOService;
    UserClient userClient;
    RestaurantMapper mapper;

    @Override
    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest request, MultipartFile image) {
        if (restaurantRepository.existsByName(request.name()) ||  restaurantRepository.existsBySlug(request.slug()))
            throw new MerchantException(MerchantErrorCode.RESTAURANT_EXIST);

        UserDTO user = userClient.getUserById(request.ownerId());
        if (user == null || !"MERCHANT".equals(user.role()))
            throw new MerchantException(MerchantErrorCode.INVALID_MERCHANT_USER_ACCOUNT);

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = minIOService.uploadFile(image);
        }

        Set<Cuisine> cuisines = new HashSet<>();
        if (request.cuisinesId() != null && !request.cuisinesId().isEmpty()) {
            List<Cuisine> cuisinesResult = cuisineRepository.findAllById(request.cuisinesId());
            cuisines.addAll(cuisinesResult);
        }

        Restaurant restaurant = Restaurant.builder()
                .name(request.name())
                .ownerId(user.id())
                .slug(request.slug())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .phone(request.phone())
                .description(request.description())
                .imageFileUrl(imageUrl)
                .ratingAverage(0.0)
                .reviewCount(0)
                .isActive(true)
                .isOpen(true)
                .cuisines(cuisines)
                .build();

        Restaurant result = restaurantRepository.save(restaurant);

        return mapper.response(result);
    }

    @Override
    @Transactional
    @CacheEvict(value = "restaurant_details", key = "#id")
    public RestaurantResponse updateRestaurantById(Long id, RestaurantPatchRequest request, MultipartFile image) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        if (request.name() != null && !request.name().equals(restaurant.getName())) {
            if (restaurantRepository.existsByName(request.name())) {
                throw new MerchantException(MerchantErrorCode.RESTAURANT_EXIST);
            }
            restaurant.setName(request.name());
        }

        if (request.slug() != null &&  !request.slug().equals(restaurant.getSlug())) {
            if (restaurantRepository.existsBySlug(request.slug())) {
                throw new MerchantException(MerchantErrorCode.SLUG_EXIST);
            }
            restaurant.setSlug(request.slug());
        }

        if (request.address() != null)
            restaurant.setAddress(request.address());

        if (request.phone() != null)
            restaurant.setPhone(request.phone());

        if (request.description() != null)
            restaurant.setDescription(request.description());

        if (request.ownerId() != null) {
            UserDTO user = userClient.getUserById(request.ownerId());
            if (user == null || !"MERCHANT".equals(user.role())) {
                throw new MerchantException(MerchantErrorCode.INVALID_MERCHANT_USER_ACCOUNT);
            }
            restaurant.setOwnerId(user.id());
        }

        if (request.latitude() != null)
            restaurant.setLatitude(request.latitude());

        if (request.longitude() != null)
            restaurant.setLongitude(request.longitude());

        if (request.isActive() != null)
            restaurant.setActive(request.isActive());

        if (request.isOpen() != null)
            restaurant.setOpen(request.isOpen());

        if (request.cuisinesId() != null && !request.cuisinesId().isEmpty()) {
            List<Cuisine> cuisinesResult = cuisineRepository.findAllById(request.cuisinesId());
            restaurant.setCuisines(new HashSet<>(cuisinesResult));
        }

        if (image != null && !image.isEmpty()) {
            if (restaurant.getImageFileUrl() != null && !restaurant.getImageFileUrl().isEmpty()) {
                minIOService.deleteFile(restaurant.getImageFileUrl());
            }
            String newImageUrl = minIOService.uploadFile(image);
            restaurant.setImageFileUrl(newImageUrl);
        }

        Restaurant result =  restaurantRepository.save(restaurant);

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "restaurant_details", key = "#id")
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        return mapper.response(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getAllRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Restaurant> result = restaurantRepository.findAll(pageable);

        return result.map(mapper::response);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getAllRestaurantsByCuisine(int page, int size, String cuisineSlug) {
        Cuisine cuisine = cuisineRepository.findBySlug(cuisineSlug)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.CUISINE_NOT_EXIST));


        Pageable pageable = PageRequest.of(page, size);
        Page<Restaurant> restaurants = restaurantRepository.findByCuisinesContaining(cuisine, pageable);

        return restaurants.map(mapper::response);
    }

    @Override
    @Transactional
    @CacheEvict(value = "restaurant_details", key = "#id")
    public void deleteRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        if (restaurant.getImageFileUrl() != null && !restaurant.getImageFileUrl().isEmpty()) {
            minIOService.deleteFile(restaurant.getImageFileUrl());
        }

        restaurantRepository.delete(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getNearbyRestaurants(Double lat, Double lon, Double radius, String cuisineSlug, int page, int size) {
        if (lat == null || lon == null) {
            throw new MerchantException(MerchantErrorCode.COORDINATE_NOT_VALID);
        }

        double searchRadius = (radius != null && radius > 0) ? radius : 5.0;
        Pageable pageable = PageRequest.of(page, size);
        Page<Restaurant> restaurantPage;

        if (cuisineSlug != null && !cuisineSlug.isEmpty()) {
            restaurantPage = restaurantRepository.findNearbyRestaurantsByCuisine(lat, lon, searchRadius, cuisineSlug, pageable);
        } else {
            restaurantPage = restaurantRepository.findNearbyRestaurants(lat, lon, searchRadius, pageable);
        }

        return restaurantPage.map(mapper::response);
    }
}
