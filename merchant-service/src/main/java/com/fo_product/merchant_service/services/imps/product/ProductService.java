package com.fo_product.merchant_service.services.imps.product;

import com.fo_product.merchant_service.dtos.requests.product.ProductPatchRequest;
import com.fo_product.merchant_service.dtos.requests.product.ProductRequest;
import com.fo_product.merchant_service.dtos.responses.ProductResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;
import com.fo_product.merchant_service.mappers.ProductMapper;
import com.fo_product.merchant_service.models.entities.product.Category;
import com.fo_product.merchant_service.models.entities.product.Product;
import com.fo_product.merchant_service.models.repositories.product.CategoryRepository;
import com.fo_product.merchant_service.models.repositories.product.ProductRepository;
import com.fo_product.merchant_service.services.interfaces.IMinIOService;
import com.fo_product.merchant_service.services.interfaces.product.IProductService;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService implements IProductService {
    IMinIOService minioService;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = "product_details", key = "#id")
    public ProductResponse createProduct(ProductRequest request, MultipartFile image) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.CATEGORY_NOT_EXIST));

        String imageUrl = null;
        if (!image.isEmpty() && image != null) {
            imageUrl = minioService.uploadFile(image);
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .originalPrice(request.originalPrice())
                .imageUrl(imageUrl)
                .status(true)
                .category(category)
                .build();

        Product result = productRepository.save(product);

        return mapper.response(result);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product_details", key = "#id")
    public ProductResponse updateProduct(Long id, ProductPatchRequest request, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.PRODUCT_NOT_EXIST));

        if (request.name() != null)
            product.setName(request.name());

        if (request.description() != null)
            product.setDescription(request.description());

        if (request.price() != null)
            product.setPrice(request.price());

        if (request.originalPrice() != null)
            product.setOriginalPrice(request.originalPrice());

        if (request.status() != null)
            product.setStatus(request.status());

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new MerchantException(MerchantExceptionCode.CATEGORY_NOT_EXIST));

            product.setCategory(category);
        }

        if (!image.isEmpty() && image != null) {
            String imageUrl = minioService.uploadFile(image);
            product.setImageUrl(imageUrl);
        }

        Product result = productRepository.save(product);

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "product_details", key = "#id")
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.PRODUCT_NOT_EXIST));

        return mapper.response(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(mapper::response);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product_details", key = "#id")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.PRODUCT_NOT_EXIST));

        productRepository.delete(product);
    }
}
