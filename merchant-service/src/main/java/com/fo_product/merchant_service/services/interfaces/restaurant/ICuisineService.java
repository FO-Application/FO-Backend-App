package com.fo_product.merchant_service.services.interfaces.restaurant;

import com.fo_product.merchant_service.dtos.requests.cuisine.CuisinePatchRequest;
import com.fo_product.merchant_service.dtos.requests.cuisine.CuisineRequest;
import com.fo_product.merchant_service.dtos.responses.CuisineResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICuisineService {
    CuisineResponse createCuisine(CuisineRequest request, MultipartFile image);
    CuisineResponse updateCuisineById(Long id, CuisinePatchRequest request, MultipartFile image);
    CuisineResponse getCuisineById(Long id);
    List<CuisineResponse> getAllCuisines();
    void deleteCuisineById(Long id);
}
