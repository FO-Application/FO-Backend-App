package com.fo_product.user_service.services.imps;

import com.fo_product.user_service.exceptions.code.UserErrorCode;
import com.fo_product.user_service.exceptions.UserException;
import com.fo_product.user_service.mappers.UserMapper;
import com.fo_product.user_service.models.entities.Role;
import com.fo_product.user_service.models.entities.User;
import com.fo_product.user_service.models.enums.OtpTokenType;
import com.fo_product.user_service.models.enums.UserStatus;
import com.fo_product.user_service.models.hashes.PendingUser;
import com.fo_product.user_service.models.repositories.PendingUserRepository;
import com.fo_product.user_service.models.repositories.RoleRepository;
import com.fo_product.user_service.models.repositories.UserRepository;
import com.fo_product.user_service.dtos.requests.*;
import com.fo_product.user_service.dtos.responses.AuthenticationDTO;
import com.fo_product.user_service.dtos.responses.PendingUserResponse;
import com.fo_product.user_service.dtos.responses.UserResponse;
import com.fo_product.user_service.services.interfaces.IAuthService;
import com.fo_product.user_service.services.interfaces.IJwtService;
import com.fo_product.user_service.services.interfaces.IOtpService;
import com.fo_product.user_service.services.interfaces.IPendingUserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    IJwtService jwtService;
    IPendingUserService pendingUserService;
    IOtpService otpService;
    PendingUserRepository pendingUserRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;

    @Override
    @Transactional
    public PendingUserResponse createPendingUser(UserRequest request, String role) {
        if (request == null)
            throw new UserException(UserErrorCode.REQUEST_NULL);

        if (userRepository.existsByEmail(request.email()) ||
                userRepository.existsByPhone(request.phone()))
            throw new UserException(UserErrorCode.ACCOUNT_EXIST);

        PendingUser pendingUser = PendingUser.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .dob(request.dob())
                .role(role)
                .expiredTime(15L)
                .build();

        PendingUserResponse response = pendingUserService.savePendingUser(pendingUser);
        try {
            otpService.generateAndSendOtp(pendingUser.getEmail(), OtpTokenType.REGISTER);
        } catch (UserException e) {
            pendingUserRepository.deleteById(pendingUser.getEmail());
            throw new UserException(UserErrorCode.SEND_MAIL_FAILED);
        }

        return response;
    }

    @Override
    @Transactional
    @CacheEvict(value = "cacheUsers", allEntries = true)
    public UserResponse verifyAndCreateUser(VerifyOtpRequest request) {
        if (request == null) throw new UserException(UserErrorCode.REQUEST_NULL);

        String email = request.email();
        String otpCode = request.otpCode();

        PendingUser pendingUser = pendingUserService.getPendingUser(email);

        boolean verified = otpService.verifyOtp(email, otpCode);
        if (!verified) throw new UserException(UserErrorCode.VERIFY_OTP_FAILED);

        // Tạo User và lưu vào DB
        User user = User.builder()
                .email(pendingUser.getEmail())
                .password(pendingUser.getPassword())
                .firstName(pendingUser.getFirstName())
                .lastName(pendingUser.getLastName())
                .phone(pendingUser.getPhone())
                .dob(pendingUser.getDob())
                .userStatus(UserStatus.ACTIVE)
                .build();

        Role role = roleRepository.findByName(pendingUser.getRole())
                .orElseThrow(() -> new UserException(UserErrorCode.ROLE_NOT_EXIST));

        user.setRole(role);

        User result = userRepository.save(user);
        pendingUserRepository.deleteById(email);

        return userMapper.response(result);
    }

    @Override
    public void resendOtp(EmailRequest request) {
        if (request == null) throw new UserException(UserErrorCode.REQUEST_NULL);

        String email = request.email();
        OtpTokenType type = request.type();

        // LOGIC GỘP CHUNG Ở ĐÂY
        switch (type) {
            case REGISTER -> {
                // Nếu là Đăng ký -> Phải check trong bảng Pending (User chưa kích hoạt)
                if (!pendingUserRepository.existsById(email)) {
                    throw new UserException(UserErrorCode.PENDING_USER_NOT_FOUND);
                }
            }
            case FORGOT_PASSWORD -> {
                // Nếu là Quên mật khẩu -> Phải check trong bảng User (User đã tồn tại)
                if (!userRepository.existsByEmail(email)) {
                    throw new UserException(UserErrorCode.USER_NOT_EXIST);
                }
            }
            default -> throw new UserException(UserErrorCode.INVALID_OTP_TYPE);
        }

        // Nếu check OK thì tạo và gửi OTP (Hàm này đã xử lý việc gửi đúng template mail dựa theo type rồi)
        otpService.generateAndSendOtp(email, type);

        log.info("Resent OTP to email: {} with type: {}", email, type);
    }

    @Override
    public AuthenticationDTO authentication(AuthenticateRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_EXIST));

        boolean matches = passwordEncoder.matches(request.password(), user.getPassword());
        if (!matches) throw new UserException(UserErrorCode.UNAUTHENTICATED);

        String role = user.getRole().getName();

        JwtService.TokenPair token = jwtService.generateTokenPair(user);
        return AuthenticationDTO.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .role(role)
                .build();
    }

    @Override
    public AuthenticationDTO refreshToken(String refreshToken) throws ParseException, JOSEException {
        if (refreshToken == null) throw new UserException(UserErrorCode.UNAUTHENTICATED);

        Map<String, Object> result = jwtService.refreshToken(refreshToken);
        return AuthenticationDTO.builder()
                .accessToken(result.get("accessToken").toString())
                .refreshToken(result.get("refreshToken").toString())
                .role(result.get("role").toString())
                .build();
    }

    @Override
    public void sendForgotPasswordOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_EXIST));

        otpService.generateAndSendOtp(email, OtpTokenType.FORGOT_PASSWORD);
    }

    @Override
    @Transactional
    public void forgotPassword(NewPasswordRequest request) {
        boolean verifiedOtp = otpService.verifyOtp(request.email(), request.otp());
        if (!verifiedOtp)
            throw new UserException(UserErrorCode.VERIFY_OTP_FAILED);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_EXIST));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    public void logout(String refreshToken) throws ParseException, JOSEException {
        if (refreshToken == null) throw new UserException(UserErrorCode.UNAUTHENTICATED);

        SignedJWT token = jwtService.verifyToken(refreshToken, "refresh");
        jwtService.invalidatedToken(token);
    }
}
