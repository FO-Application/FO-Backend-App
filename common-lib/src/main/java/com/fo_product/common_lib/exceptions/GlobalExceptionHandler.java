package com.fo_product.common_lib.exceptions;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.common_lib.exceptions.codes.CommonErrorCode;
import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import com.fo_product.common_lib.exceptions.codes.ValidationCode;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Custom Business Exception (AppException, ProductException...)
    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<APIResponse<Object>> handlingAppException(BaseException baseException) {
        ErrorCode errorCode = baseException.getErrorCode();

        APIResponse<Object> apiResponse = new APIResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(apiResponse);
    }

    // 2. Handle Validation Exception (@NotBlank, @Email...)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Object>> handlingValidationException(MethodArgumentNotValidException methodArgumentNotValidException){
        Map<String, String> errors = new HashMap<>();
        int code = HttpStatus.BAD_REQUEST.value();

        for(FieldError error : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            String field = error instanceof FieldError ? error.getField() : "unknown error";
            String errorCodeStr = error.getDefaultMessage(); // String key: "INVALID_EMAIL"

            ValidationCode validationCode = getValidationCode(errorCodeStr);
            String message = validationCode.getMessage();

            // Unwrap logic to get attributes (min, max...)
            try {
                ConstraintViolation<?> violation = error.unwrap(ConstraintViolation.class);
                Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();
                message = mapAttributesToMessage(message, attributes);
            } catch (Exception exception) {
                // log.debug("Cannot unwrap constraint violation", exception);
            }

            errors.put(field, message);
            code = validationCode.getCode();
        }

        return ResponseEntity
                .badRequest()
                .body(new APIResponse<>(code, "Validation failed", errors));
    }

    // 3. Handle Uncategorized Exception (Lỗi 500, NullPointer, DB Error...)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<APIResponse<Object>> handlingUnknownException(Exception exception) {
        log.error("Uncategorized error: ", exception); // Nên log lỗi ra để debug

        APIResponse<Object> apiResponse = new APIResponse<>();
        // Sử dụng CommonErrorCode thay vì AppCode
        ErrorCode errorCode = CommonErrorCode.UNCATEGORIZED_EXCEPTION;

        apiResponse.setCode(errorCode.getCode());
        // Có thể trả về message gốc của exception để debug, hoặc ẩn đi nếu muốn bảo mật
        apiResponse.setMessage(exception.getMessage() != null ? exception.getMessage() : errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(apiResponse);
    }

    // --- Helper Methods ---

    private ValidationCode getValidationCode(String errorCode) {
        try {
            return ValidationCode.valueOf(errorCode);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ValidationCode.UNKNOWN_VALIDATOR;
        }
    }

    private String mapAttributesToMessage(String template, Map<String, Object> attributes) {
        String result = template;
        for(Map.Entry<String, Object> entry : attributes.entrySet()) {
            String placeHolder = "{" + entry.getKey() + "}";
            if (result.contains(placeHolder)) {
                result = result.replace(placeHolder, String.valueOf(entry.getValue()));
            }
        }
        return result;
    }
}