package com.fo_product.user_service.services.imps;

import com.fo_product.user_service.exceptions.code.UserExceptionCode;
import com.fo_product.user_service.exceptions.UserException;
import com.fo_product.user_service.mappers.UserMapper;
import com.fo_product.user_service.models.entities.User;
import com.fo_product.user_service.models.repositories.UserRepository;
import com.fo_product.user_service.dtos.requests.UserPatchRequest;
import com.fo_product.user_service.dtos.responses.UserResponse;
import com.fo_product.user_service.services.interfaces.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {
    UserRepository userRepository;
    UserMapper userMapper;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "cacheUsers", allEntries = true), // Xóa cache list (vì data thay đổi, list cũ sai)
            @CacheEvict(value = "user_details", key = "#id")      // Xóa cache của RIÊNG user này để lần sau getById nó load cái mới
    })
    public UserResponse updateUserById(Long id, UserPatchRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_EXIST));

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new UserException(UserExceptionCode.EMAIL_EXIST);
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

        return userMapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user_details", key = "#id")
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_EXIST));

        return userMapper.response(user);
    }

    @Override
    @Cacheable(value = "cacheUsers")
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(userMapper::response);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "cacheUsers", allEntries = true), // Xóa cache list (vì data thay đổi, list cũ sai)
            @CacheEvict(value = "user_details", key = "#id")      // Xóa cache của RIÊNG user này để lần sau getById nó load cái mới
    })
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_EXIST));

        userRepository.delete(user);
    }

}
