package com.fo_product.user_service.services.interfaces;

import com.fo_product.user_service.resources.requests.UserPatchRequest;
import com.fo_product.user_service.resources.requests.UserRequest;
import com.fo_product.user_service.resources.responses.UserResponse;
import org.springframework.data.domain.Page;

public interface IUserService {
    UserResponse createUser(UserRequest request);
    UserResponse updateUserById(Long id, UserPatchRequest request);
    UserResponse getUserById(Long id);
    Page<UserResponse> getAllUsers(int page, int size);
    void deleteUserById(Long id);
}
