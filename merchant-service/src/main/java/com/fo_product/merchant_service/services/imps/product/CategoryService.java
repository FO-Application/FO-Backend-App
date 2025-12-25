package com.fo_product.merchant_service.services.imps.product;

import com.fo_product.merchant_service.dtos.requests.category.CategoryPatchRequest;
import com.fo_product.merchant_service.dtos.requests.category.CategoryRequest;
import com.fo_product.merchant_service.dtos.responses.product.CategoryResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantErrorCode;
import com.fo_product.merchant_service.mappers.product.CategoryMapper;
import com.fo_product.merchant_service.models.entities.product.Category;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.repositories.product.CategoryRepository;
import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantRepository;
import com.fo_product.merchant_service.services.interfaces.product.ICategoryService;
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
public class CategoryService implements ICategoryService {
    CategoryRepository categoryRepository;
    RestaurantRepository restaurantRepository;
    CategoryMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = "cacheCategories", allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.idRestaurant())
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        if (categoryRepository.existsByNameAndRestaurant(request.name(), restaurant)) {
            throw new MerchantException(MerchantErrorCode.CATEGORY_EXIST);
        }

        Category category = Category.builder()
                .name(request.name())
                .displayOrder(request.displayOrder())
                .isActive(true)
                .restaurant(restaurant)
                .build();

        Category result = categoryRepository.save(category);

        return mapper.response(result);
    }

    @Override
    @Transactional
    @Caching(evict =
            {
                    @CacheEvict(value = "cacheCategories",  allEntries = true),
                    @CacheEvict(value = "category_details", key = "#id")
            }
    )
    public CategoryResponse updateCategory(Long id, CategoryPatchRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.CATEGORY_NOT_EXIST));

        if (request.name() != null && !request.name().equals(category.getName())) {
            Restaurant restaurant = category.getRestaurant();
            if (categoryRepository.existsByNameAndRestaurant(request.name(), restaurant)) {
                throw new MerchantException(MerchantErrorCode.CATEGORY_EXIST);
            }
            category.setName(request.name());
        }

        if (request.displayOrder() != null)
            category.setDisplayOrder(request.displayOrder());

        if (request.isActive() != null)
            category.setActive(request.isActive());

        Category result = categoryRepository.save(category);

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cacheCategories", key = "#restaurantSlug")
    public List<CategoryResponse> getAllCategories(String restaurantSlug) {
        Restaurant restaurant = restaurantRepository.findBySlug(restaurantSlug)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        List<Category> result = categoryRepository.findAllByRestaurantOrderByDisplayOrderAsc(restaurant);

        return result.stream().map(mapper::response).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "category_details", key = "#id")
    public CategoryResponse getCategoryByRestaurant(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.CATEGORY_NOT_EXIST));

        return mapper.response(category);
    }

    @Override
    @Transactional
    @Caching(evict =
            {
                    @CacheEvict(value = "cacheCategories",  allEntries = true),
                    @CacheEvict(value = "category_details", key = "#id")
            }
    )
    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.CATEGORY_NOT_EXIST));

        categoryRepository.delete(category);
    }
}
