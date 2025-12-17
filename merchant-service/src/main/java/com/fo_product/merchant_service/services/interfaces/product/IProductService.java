package com.fo_product.merchant_service.services.interfaces.product;

import com.fo_product.merchant_service.dtos.requests.product.ProductPatchRequest;
import com.fo_product.merchant_service.dtos.requests.product.ProductRequest;
import com.fo_product.merchant_service.dtos.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    ProductResponse createProduct(ProductRequest request, MultipartFile image);
    ProductResponse updateProduct(Long id, ProductPatchRequest request, MultipartFile image);
    ProductResponse getById(Long id);
    List<ProductResponse> getAllProductsByCategory(Long categoryId);
    void deleteProduct(Long id);
}
