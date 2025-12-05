package com.fo_product.merchant_service.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface IMinIOService {
    String uploadFile(MultipartFile file);
    void deleteFile(String fileName);
    String getPublicUrl(String fileName);
}
