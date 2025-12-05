package com.fo_product.common_lib.exceptions.validation.validator;

import com.fo_product.common_lib.exceptions.validation.constraint.PhoneConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PhoneValidator implements ConstraintValidator<PhoneConstraint, String> {
    private int length;

    @Override
    public void initialize(PhoneConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        length = constraintAnnotation.length();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) return true;
        int phoneLength = value.length();
        return value.matches("\\d{" + length + "}");
    }
}
