package com.fo_product.merchant_service.services.interfaces.product;

import com.fo_product.merchant_service.dtos.requests.category.CategoryPatchRequest;
import com.fo_product.merchant_service.dtos.requests.category.CategoryRequest;
import com.fo_product.merchant_service.dtos.responses.CategoryResponse;
import org.springframework.data.domain.Page;

public interface ICategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryPatchRequest request);
    Page<CategoryResponse> getAllCategories(int page, int size);
    CategoryResponse getCategoryById(Long id);
    void deleteCategoryById(Long id);
}
