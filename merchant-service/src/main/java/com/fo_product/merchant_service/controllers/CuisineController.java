package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.requests.cuisine.CuisinePatchRequest;
import com.fo_product.merchant_service.dtos.requests.cuisine.CuisineRequest;
import com.fo_product.merchant_service.dtos.responses.CuisineResponse;
import com.fo_product.merchant_service.services.interfaces.ICuisineService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuisine")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CuisineController {
    ICuisineService cuisineService;

    @PostMapping
    APIResponse<CuisineResponse> createCuisine(
            @RequestPart("data") @Valid CuisineRequest request,
            @RequestPart("image") MultipartFile image
    ) {
        CuisineResponse result = cuisineService.createCuisine(request, image);
        return APIResponse.<CuisineResponse>builder()
                .result(result)
                .message("Create cuisine")
                .build();
    }

    @PutMapping("/{cuisineId}")
    APIResponse<CuisineResponse> updateCuisine(
            @PathVariable("cuisineId") Long id,
            @RequestPart("data") CuisinePatchRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        CuisineResponse result = cuisineService.updateCuisineById(id, request, image);
        return APIResponse.<CuisineResponse>builder()
                .result(result)
                .message("Update cuisine")
                .build();
    }

    @GetMapping("/{cuisineId}")
    APIResponse<CuisineResponse> getCuisineById(@PathVariable("cuisineId") Long id) {
        CuisineResponse result = cuisineService.getCuisineById(id);
        return APIResponse.<CuisineResponse>builder()
                .result(result)
                .message("Get cuisine by id")
                .build();
    }

    @GetMapping
    APIResponse<List<CuisineResponse>> getAllCuisines() {
        List<CuisineResponse> result = cuisineService.getAllCuisines();
        return APIResponse.<List<CuisineResponse>>builder()
                .result(result)
                .message("Get all cuisines")
                .build();
    }

    @DeleteMapping("/{cuisineId}")
    APIResponse<?> deleteCuisine(@PathVariable("cuisineId") Long id) {
        cuisineService.deleteCuisineById(id);
        return APIResponse.builder()
                .message("Delete cuisine")
                .build();
    }
}
