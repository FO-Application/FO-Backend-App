package com.fo_product.merchant_service.exceptions.codes;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MinIOErrorCode implements ErrorCode {
        INITIALIZING_BUCKET_FAILED(3001, "Error initializing MinIO bucket", HttpStatus.INTERNAL_SERVER_ERROR),
        UPLOAD_FILE_FAILED(3002, "Error uploading file to MinIO", HttpStatus.INTERNAL_SERVER_ERROR),
        DELETE_FILE_FAILED(3003, "Error deleting file from MinIO", HttpStatus.INTERNAL_SERVER_ERROR),

    ;
    int code;
    String message;
    HttpStatus status;
}
