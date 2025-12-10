package com.fo_product.merchant_service.services.interfaces;

import com.fo_product.merchant_service.dtos.requests.cuisine.CuisinePatchRequest;
import com.fo_product.merchant_service.dtos.requests.cuisine.CuisineRequest;
import com.fo_product.merchant_service.dtos.responses.CuisineResponse;

import java.util.List;

public interface ICuisineService {
    CuisineResponse createCuisine(CuisineRequest request);
    CuisineResponse updateCuisineById(Long id, CuisinePatchRequest request);
    CuisineResponse getCuisineById(Long id);
    List<CuisineResponse> getAllCuisines();
    void deleteCuisineById(Long id);
}
