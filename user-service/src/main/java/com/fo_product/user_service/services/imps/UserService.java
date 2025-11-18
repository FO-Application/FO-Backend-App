package com.fo_product.user_service.services.imps;

import com.fo_product.user_service.exceptions.code.AppCode;
import com.fo_product.user_service.exceptions.custom.AppException;
import com.fo_product.user_service.models.entities.User;
import com.fo_product.user_service.models.repositories.UserRepository;
import com.fo_product.user_service.resources.requests.UserPatchRequest;
import com.fo_product.user_service.resources.requests.UserRequest;
import com.fo_product.user_service.resources.responses.UserResponse;
import com.fo_product.user_service.services.interfaces.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @CacheEvict(value = "cacheUsers", allEntries = true)
    public UserResponse createUser(UserRequest request) {
        if (request == null) throw new AppException(AppCode.REQUEST_NULL);
        if (userRepository.existsByEmail(request.email())) throw new AppException(AppCode.USER_EXIST);

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .dob(request.dob())
                .build();

        User result = userRepository.save(user);

        return response(result);
    }

    @Override
    @Transactional
    @CacheEvict(value = "cacheUsers", allEntries = true)
    public UserResponse updateUserById(Long id, UserPatchRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(AppCode.USER_NOT_EXIST));

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new AppException(AppCode.EMAIL_EXIST);
            }

            user.setEmail(request.email());
        }

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        if (request.dob() != null) {
            user.setDob(request.dob());
        }

        User result = userRepository.save(user);

        return response(result);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(AppCode.USER_NOT_EXIST));

        return response(user);
    }

    @Override
    @Cacheable(value = "cacheUsers")
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(this::response);
    }

    @Override
    @Transactional
    @CacheEvict(value = "cacheUsers", allEntries = true)
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(AppCode.USER_NOT_EXIST));

        userRepository.delete(user);
    }

    private UserResponse response(User user) {
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
