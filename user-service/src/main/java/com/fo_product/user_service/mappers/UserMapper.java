package com.fo_product.user_service.mappers;

import com.fo_product.user_service.models.entities.User;
import com.fo_product.user_service.resources.responses.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse response(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .dob(user.getDob())
                .build();
    }
}
