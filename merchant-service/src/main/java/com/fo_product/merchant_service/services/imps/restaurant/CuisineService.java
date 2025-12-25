package com.fo_product.merchant_service.services.imps.restaurant;

import com.fo_product.merchant_service.dtos.requests.cuisine.CuisinePatchRequest;
import com.fo_product.merchant_service.dtos.requests.cuisine.CuisineRequest;
import com.fo_product.merchant_service.dtos.responses.restaurant.CuisineResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantErrorCode;
import com.fo_product.merchant_service.mappers.restaurant.CuisineMapper;
import com.fo_product.merchant_service.models.entities.restaurant.Cuisine;
import com.fo_product.merchant_service.models.repositories.restaurant.CuisineRepository;
import com.fo_product.merchant_service.services.interfaces.restaurant.ICuisineService;
import com.fo_product.merchant_service.services.interfaces.IMinIOService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CuisineService implements ICuisineService {
    CuisineRepository cuisineRepository;
    IMinIOService minIOService;
    CuisineMapper mapper;

    // 1. KHI TẠO MỚI (CREATE)
    // Chỉ cần xóa cache danh sách (cacheCuisines) để người dùng load lại thấy món mới.
    // Không cần xóa cache chi tiết vì món này mới tinh, chưa từng được cache.
    @Override
    @Transactional
    @CacheEvict(value = "cacheCuisines", allEntries = true)
    public CuisineResponse createCuisine(CuisineRequest request, MultipartFile image) {
        if (cuisineRepository.existsBySlug(request.slug()) || cuisineRepository.existsByName(request.name()))
            throw new MerchantException(MerchantErrorCode.CUISINE_EXIST);

        String imageUrl = null;
        if (image != null && !image.isEmpty())
            imageUrl = minIOService.uploadFile(image);

        Cuisine cuisine = Cuisine.builder()
                .name(request.name())
                .slug(request.slug())
                .imageFileUrl(imageUrl)
                .build();

        Cuisine result = cuisineRepository.save(cuisine);

        return mapper.response(result);
    }

    // 2. KHI CẬP NHẬT (UPDATE)
    // Cần xóa cả 2 nơi:
    // - cacheCuisines: Xóa danh sách vì tên món có thể đổi -> thứ tự sắp xếp đổi.
    // - cuisine_details: Xóa cache của chính món này (theo ID) để lần sau getById nó load thông tin mới.
    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "cacheCuisines", allEntries = true),
                    @CacheEvict(value = "cuisine_details", key = "#id")
            }
    )
    public CuisineResponse updateCuisineById(Long id, CuisinePatchRequest request, MultipartFile image) {
        Cuisine cuisine = cuisineRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.CUISINE_NOT_EXIST));

        if (request.name() != null && !request.name().equals(cuisine.getName())) {
            if (cuisineRepository.existsByName(request.name())) {
                throw new MerchantException(MerchantErrorCode.CUISINE_EXIST);
            }
            cuisine.setName(request.name());
        }

        if (request.slug() != null  && !request.slug().equals(cuisine.getSlug())) {
            if (cuisineRepository.existsBySlug(request.slug())) {
                throw new MerchantException(MerchantErrorCode.SLUG_EXIST);
            }
            cuisine.setSlug(request.slug());
        }

        if (image != null && !image.isEmpty()) {
            if (cuisine.getImageFileUrl() != null && !cuisine.getImageFileUrl().isEmpty()) {
                minIOService.deleteFile(cuisine.getImageFileUrl());
            }
            String newImageUrl = minIOService.uploadFile(image);
            cuisine.setImageFileUrl(newImageUrl);
        }

        Cuisine result = cuisineRepository.save(cuisine);

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cuisine_details", key = "#id")
    public CuisineResponse getCuisineById(Long id) {
        Cuisine cuisine = cuisineRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.CUISINE_NOT_EXIST));
        return mapper.response(cuisine);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cacheCuisines")
    public List<CuisineResponse> getAllCuisines() {
        List<Cuisine> cuisines = cuisineRepository.findAll();

        return cuisines.stream().map(mapper::response).toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "cacheCuisines", allEntries = true),
            @CacheEvict(value = "cuisine_details", key = "#id")
    })
    public void deleteCuisineById(Long id) {
        Cuisine cuisine = cuisineRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.CUISINE_NOT_EXIST));

        if (cuisine.getImageFileUrl() != null && !cuisine.getImageFileUrl().isEmpty()) {
            minIOService.deleteFile(cuisine.getImageFileUrl());
        }

        cuisineRepository.delete(cuisine);
    }
}
