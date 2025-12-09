package com.fo_product.merchant_service.services.interfaces;

import com.fo_product.merchant_service.dtos.requests.cuisine.CuisinePatchRequest;
import com.fo_product.merchant_service.dtos.requests.cuisine.CuisineRequest;
import com.fo_product.merchant_service.dtos.responses.CuisineResponse;
import org.springframework.data.domain.Page;

public interface ICuisineService {
    CuisineResponse createCuisine(CuisineRequest request);
    CuisineResponse updateCuisineById(Long id, CuisinePatchRequest request);
    CuisineResponse getCuisineById(Long id);
    Page<CuisineResponse> getAllCuisines(int page, int size);
    void deleteCuisineById(Long id);
}
