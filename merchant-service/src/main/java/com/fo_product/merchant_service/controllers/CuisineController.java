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
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cuisine")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CuisineController {
    ICuisineService cuisineService;

    @PostMapping
    APIResponse<CuisineResponse> createCuisine(@RequestBody @Valid CuisineRequest request) {
        CuisineResponse result = cuisineService.createCuisine(request);
        return APIResponse.<CuisineResponse>builder()
                .result(result)
                .message("Create cuisine")
                .build();
    }

    @PutMapping("/{cuisineId}")
    APIResponse<CuisineResponse> updateCuisine(
            @PathVariable("cuisineId") Long id,
            @RequestBody CuisinePatchRequest request
    ) {
        CuisineResponse result = cuisineService.updateCuisineById(id, request);
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
    APIResponse<Page<CuisineResponse>> getAllCuisines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CuisineResponse> result = cuisineService.getAllCuisines(page, size);
        return APIResponse.<Page<CuisineResponse>>builder()
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
