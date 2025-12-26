package com.fo_product.merchant_service.controllers;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileController {
    MinioClient minioClient;

    // 2. Thêm regex :.+ vào sau fileName để Spring không bỏ qua đuôi .jpg, .png
    @GetMapping("/{bucketName}/{fileName:.+}")
    public ResponseEntity<InputStreamResource> viewFile(
            @PathVariable String bucketName,
            @PathVariable String fileName
    ) {
        try {
            log.info("Đang yêu cầu file: Bucket = {}, File = {}", bucketName, fileName);

            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(stat.contentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(stream));

        } catch (Exception e) {
            log.error("Lỗi khi lấy file từ MinIO: ", e);
            return ResponseEntity.notFound().build();
        }
    }
}