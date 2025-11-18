package com.fo_product.user_service.resources.requests;

import java.time.LocalDate;

public record UserPatchRequest(
        String email,
        String firstName,
        String lastName,
        String phone,
        LocalDate dob
) {
}
