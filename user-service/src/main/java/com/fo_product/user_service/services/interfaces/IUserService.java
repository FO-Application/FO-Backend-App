package com.fo_product.user_service.services.interfaces;

import com.fo_product.user_service.dtos.requests.UserPatchRequest;
import com.fo_product.user_service.dtos.responses.UserResponse;
import org.springframework.data.domain.Page;

public interface IUserService {
    UserResponse updateUserById(Long id, UserPatchRequest request);
    UserResponse getUserById(Long id);
    UserResponse getMe(Long userId);
    Page<UserResponse> getAllUsers(int page, int size);
    void deleteUserById(Long id);
}
