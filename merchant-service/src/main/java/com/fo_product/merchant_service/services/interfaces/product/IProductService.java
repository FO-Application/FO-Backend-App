package com.fo_product.merchant_service.services.interfaces.product;

import com.fo_product.merchant_service.dtos.requests.product.ProductPatchRequest;
import com.fo_product.merchant_service.dtos.requests.product.ProductRequest;
import com.fo_product.merchant_service.dtos.responses.product.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    ProductResponse createProduct(ProductRequest request, MultipartFile image);
    ProductResponse updateProduct(Long id, ProductPatchRequest request, MultipartFile image);
    ProductResponse getById(Long id);
    List<ProductResponse> getAllProductsByCategory(Long categoryId);
    List<ProductResponse> getProductsByIds(List<Long> productids);
    void deleteProduct(Long id);
}
