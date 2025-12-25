package com.fo_product.merchant_service.services.interfaces.product;

import com.fo_product.merchant_service.dtos.requests.category.CategoryPatchRequest;
import com.fo_product.merchant_service.dtos.requests.category.CategoryRequest;
import com.fo_product.merchant_service.dtos.responses.product.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryPatchRequest request);
    List<CategoryResponse> getAllCategories(String restaurantSlug);
    CategoryResponse getCategoryByRestaurant(Long id);
    void deleteCategoryById(Long id);
}
