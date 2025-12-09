package com.fo_product.merchant_service.services.imps;

import com.fo_product.merchant_service.dtos.requests.cuisine.CuisinePatchRequest;
import com.fo_product.merchant_service.dtos.requests.cuisine.CuisineRequest;
import com.fo_product.merchant_service.dtos.responses.CuisineResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;
import com.fo_product.merchant_service.mappers.CuisineMapper;
import com.fo_product.merchant_service.models.entities.restaurant.Cuisine;
import com.fo_product.merchant_service.models.repositories.restaurant.CuisineRepository;
import com.fo_product.merchant_service.services.interfaces.ICuisineService;
import com.fo_product.merchant_service.services.interfaces.IMinIOService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CuisineResponse createCuisine(CuisineRequest request) {
        if (cuisineRepository.existsBySlug(request.slug()))
            throw new MerchantException(MerchantExceptionCode.SLUG_EXIST);

        String imageUrl = null;
        if (request.image() != null && !request.image().isEmpty())
            imageUrl = minIOService.uploadFile(request.image());

        Cuisine cuisine = Cuisine.builder()
                .name(request.name())
                .slug(request.slug())
                .imageFileName(imageUrl)
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
    public CuisineResponse updateCuisineById(Long id, CuisinePatchRequest request) {
        Cuisine cuisine = cuisineRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.CUISINE_NOT_EXIST));

        if (request.name() != null)
            cuisine.setName(request.name());

        if (request.slug() != null)
            cuisine.setSlug(request.slug());

        if (request.image() != null && !request.image().isEmpty()) {
            if (cuisine.getImageFileName() != null && !cuisine.getImageFileName().isEmpty()) {
                minIOService.deleteFile(cuisine.getImageFileName());
            }
            String newImageUrl = minIOService.uploadFile(request.image());
            cuisine.setImageFileName(newImageUrl);
        }

        Cuisine result = cuisineRepository.save(cuisine);

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cuisine_details", key = "#id")
    public CuisineResponse getCuisineById(Long id) {
        Cuisine cuisine = cuisineRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.CUISINE_NOT_EXIST));
        return mapper.response(cuisine);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cacheCuisines")
    public Page<CuisineResponse> getAllCuisines(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cuisine> cuisinePage = cuisineRepository.findAll(pageable);

        return cuisinePage.map(mapper::response);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "cacheCuisines", allEntries = true),
            @CacheEvict(value = "cuisine_details", key = "#id")
    })
    public void deleteCuisineById(Long id) {
        Cuisine cuisine = cuisineRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.CUISINE_NOT_EXIST));

        cuisineRepository.delete(cuisine);
    }
}
