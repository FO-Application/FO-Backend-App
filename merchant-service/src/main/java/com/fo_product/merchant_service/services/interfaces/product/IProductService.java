package com.fo_product.merchant_service.services.interfaces.product;

import com.fo_product.merchant_service.dtos.requests.product.ProductPatchRequest;
import com.fo_product.merchant_service.dtos.requests.product.ProductRequest;
import com.fo_product.merchant_service.dtos.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface IProductService {
    ProductResponse createProduct(ProductRequest request, MultipartFile image);
    ProductResponse updateProduct(Long id, ProductPatchRequest request, MultipartFile image);
    ProductResponse getById(Long id);
    Page<ProductResponse> getAllProducts(int page, int size);
    void deleteProduct(Long id);
}
