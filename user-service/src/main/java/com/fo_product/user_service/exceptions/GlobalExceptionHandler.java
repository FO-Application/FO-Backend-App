package com.fo_product.user_service.exceptions;

import com.fo_product.user_service.exceptions.code.AppCode;
import com.fo_product.user_service.exceptions.code.ValidationCode;
import com.fo_product.user_service.exceptions.custom.AppException;
import com.fo_product.user_service.resources.APIResponse;
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

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<APIResponse> handlingAppException(AppException appException) {
        AppCode appCode = appException.getAppCode();
        APIResponse apiResponse = new APIResponse();

        apiResponse.setCode(appCode.getCode());
        apiResponse.setMessage(appCode.getMessage());

        return ResponseEntity.status(appCode.getStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<APIResponse> handlingUnknownException() {
        APIResponse apiResponse = new APIResponse();
        AppCode appCode = AppCode.UNCATEGORIZED_EXCEPTION;
        apiResponse.setCode(appCode.getCode());
        apiResponse.setMessage(appCode.getMessage());

        return ResponseEntity.status(appCode.getStatus()).body(apiResponse);
    }

    //Handling Validation Exception
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<APIResponse<Object>> handlingValidationException(MethodArgumentNotValidException methodArgumentNotValidException){
        Map<String, String> errors = new HashMap<>();
        int code = HttpStatus.BAD_REQUEST.value();

        for(FieldError error : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            String field = error instanceof FieldError fieldError ? fieldError.getField() : "unknow error";
            String errorCode = error.getDefaultMessage();

            ValidationCode validationCode = getValidationCode(errorCode);
            String message = validationCode.getMessage();

            try {
                ConstraintViolation<?> violation = error.unwrap(ConstraintViolation.class);
                Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();
                message = mapAttributesToMessage(message, attributes);
            } catch (Exception exception) {
                log.debug("Cannot unwrap constraint violation or get attributes", exception);
            }
            errors.put(field, message);
            code = validationCode.getCode();
        }

        return ResponseEntity
                .badRequest()
                .body(new APIResponse<>(code, "Validation handled successfully", errors));
    }

    //Get Validation Code inside validator message
    private ValidationCode getValidationCode(String errorCode) {
        for (ValidationCode EValidationCode : ValidationCode.values()) {
            if (EValidationCode.name().equals(errorCode)) {
                return EValidationCode;
            }
        }
        return ValidationCode.UNKNOWN_VALIDATOR;
    }

    //Map attributes from business input to display into message
    private String mapAttributesToMessage(String template, Map<String, Object> attributes) {
        String result = template;
        for(Map.Entry<String, Object> entry : attributes.entrySet()) {
            String placeHolder = "{" + entry.getKey() + "}";
            result = result.replace(placeHolder, String.valueOf(entry.getValue()));
        }
        return result;
    }
}
