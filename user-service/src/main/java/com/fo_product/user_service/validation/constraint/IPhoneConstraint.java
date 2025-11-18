package com.fo_product.user_service.validation.constraint;

import com.fo_product.user_service.validation.validator.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface IPhoneConstraint {
    String message() default "Invalid phone size";

    int length();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
