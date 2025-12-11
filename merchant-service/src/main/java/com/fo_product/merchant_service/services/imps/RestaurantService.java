package com.fo_product.merchant_service.services.imps;

import com.fo_product.merchant_service.client.UserClient;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantPatchRequest;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantRequest;
import com.fo_product.merchant_service.dtos.responses.RestaurantResponse;
import com.fo_product.merchant_service.dtos.responses.UserResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;
import com.fo_product.merchant_service.mappers.RestaurantMapper;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantRepository;
import com.fo_product.merchant_service.services.interfaces.IMinIOService;
import com.fo_product.merchant_service.services.interfaces.IRestaurantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestaurantService implements IRestaurantService {
    RestaurantRepository restaurantRepository;
    IMinIOService minIOService;
    UserClient userClient;
    RestaurantMapper mapper;

    @Override
    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest request, MultipartFile image) {
        if (restaurantRepository.existsByName(request.name()) ||  restaurantRepository.existsBySlug(request.slug()))
            throw new MerchantException(MerchantExceptionCode.RESTAURANT_EXIST);

        UserResponse user = userClient.getUserById(request.ownerId());
        if (user == null || user.role() != "MERCHANT")
            throw new MerchantException(MerchantExceptionCode.INVALID_MERCHANT_USER_ACCOUNT);

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = minIOService.uploadFile(image);
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
                .build();

        Restaurant result = restaurantRepository.save(restaurant);

        return mapper.response(result);
    }

    @Override
    @Transactional
    public RestaurantResponse updateRestaurantById(Long id, RestaurantPatchRequest request, MultipartFile image) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.RESTAURANT_NOT_EXIST));

        if (request.name() != null)
            restaurant.setName(request.name());

        if (request.slug() != null)
            restaurant.setSlug(request.slug());

        if (request.address() != null)
            restaurant.setAddress(request.address());

        if (request.phone() != null)
            restaurant.setPhone(request.phone());

        if (request.description() != null)
            restaurant.setDescription(request.description());

        if (request.ownerId() != null) {
            UserResponse user = userClient.getUserById(request.ownerId());
            if (user == null || user.role() != "MERCHANT") {
                throw new MerchantException(MerchantExceptionCode.INVALID_MERCHANT_USER_ACCOUNT);
            }
            restaurant.setOwnerId(user.id());
        }

        if (request.latitude() != null)
            restaurant.setLatitude(request.latitude());

        if (request.longitude() != null)
            restaurant.setLongitude(request.longitude());

        Restaurant result =  restaurantRepository.save(restaurant);

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.RESTAURANT_NOT_EXIST));

        return mapper.response(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getAllRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Restaurant> result = restaurantRepository.findAll(pageable);

        return result.map(restaurant -> mapper.response(restaurant));
    }

    @Override
    @Transactional
    public void deleteRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.RESTAURANT_NOT_EXIST));

        restaurantRepository.delete(restaurant);
    }
}
