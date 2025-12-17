package com.fo_product.merchant_service.services.imps.product;

import com.fo_product.merchant_service.dtos.requests.product.ProductPatchRequest;
import com.fo_product.merchant_service.dtos.requests.product.ProductRequest;
import com.fo_product.merchant_service.dtos.responses.ProductResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;
import com.fo_product.merchant_service.mappers.ProductMapper;
import com.fo_product.merchant_service.models.entities.product.Category;
import com.fo_product.merchant_service.models.entities.product.Product;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.repositories.product.CategoryRepository;
import com.fo_product.merchant_service.models.repositories.product.ProductRepository;
import com.fo_product.merchant_service.services.interfaces.IMinIOService;
import com.fo_product.merchant_service.services.interfaces.product.IProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

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
    @CacheEvict(value = "cacheProducts",  allEntries = true)
    public ProductResponse createProduct(ProductRequest request, MultipartFile image) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.CATEGORY_NOT_EXIST));

        if (productRepository.existsByNameAndCategory_Restaurant(request.name(), category.getRestaurant())) {
            throw new MerchantException(MerchantExceptionCode.PRODUCT_EXIST);
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = minioService.uploadFile(image);
        }

        if (!validatePrice(request.price(), request.originalPrice())) {
            throw new MerchantException(MerchantExceptionCode.INVALID_PRICE);
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
    @Caching(
            evict = {
                    @CacheEvict(value = "product_details", key = "#id"),
                    @CacheEvict(value = "cacheProducts",  allEntries = true)
            }
    )
    public ProductResponse updateProduct(Long id, ProductPatchRequest request, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.PRODUCT_NOT_EXIST));

        BigDecimal newPrice = request.price() != null ? request.price() : product.getPrice();
        BigDecimal newOriginalPrice = request.originalPrice() != null ? request.originalPrice() : product.getOriginalPrice();

        if (!validatePrice(newPrice, newOriginalPrice)) {
            throw new MerchantException(MerchantExceptionCode.INVALID_PRICE);
        }

        if (request.name() != null && !request.name().equals(product.getName())) {
            Restaurant restaurant = product.getCategory().getRestaurant();

            if (productRepository.existsByNameAndCategory_Restaurant(request.name(), restaurant)) {
                throw new MerchantException(MerchantExceptionCode.PRODUCT_EXIST);
            }
            product.setName(request.name());
        }
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

        if (image != null && !image.isEmpty()) {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                minioService.deleteFile(product.getImageUrl());
            }
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
    @Cacheable(value = "cacheProducts", key = "#categoryId")
    public List<ProductResponse> getAllProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.CATEGORY_NOT_EXIST));

        List<Product> products = productRepository.findAllByCategory(category);

        return products.stream().map(mapper::response).toList();
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "product_details", key = "#id"),
                    @CacheEvict(value = "cacheProducts",  allEntries = true)
            }
    )
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.PRODUCT_NOT_EXIST));

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            minioService.deleteFile(product.getImageUrl());
        }

        productRepository.delete(product);
    }

    private boolean validatePrice(BigDecimal price, BigDecimal originalPrice) {
        if (originalPrice == null)
            return true;

        if (price == null)
            return true;

        return price.compareTo(originalPrice) <= 0;
    }
}
