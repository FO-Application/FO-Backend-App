package com.fo_product.user_service.services.imps;

import com.fo_product.user_service.exceptions.code.AppCode;
import com.fo_product.user_service.exceptions.custom.AppException;
import com.fo_product.user_service.models.entities.User;
import com.fo_product.user_service.models.repositories.UserRepository;
import com.fo_product.user_service.resources.requests.AuthenticateRequest;
import com.fo_product.user_service.resources.requests.TokenRequest;
import com.fo_product.user_service.resources.responses.AuthResponse;
import com.fo_product.user_service.services.interfaces.IAuthService;
import com.fo_product.user_service.services.interfaces.IJwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    IJwtService jwtService;

    @Override
    public AuthResponse authentication(AuthenticateRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(AppCode.USER_NOT_EXIST));

        boolean matches = passwordEncoder.matches(request.password(), user.getPassword());
        if (!matches) throw new AppException(AppCode.UNAUTHENTICATED);

        JwtService.TokenPair token = jwtService.generateTokenPair(user);
        return AuthResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }

    @Override
    public AuthResponse refreshToken(TokenRequest request) throws ParseException, JOSEException {
        if (request == null) throw new AppException(AppCode.UNAUTHENTICATED);
        String rt = request.refreshToken();

        JwtService.TokenPair tokenPair = jwtService.refreshToken(rt);
        return new AuthResponse(tokenPair.getAccessToken(), tokenPair.getRefreshToken());
    }

    @Override
    public void logout(TokenRequest request) throws ParseException, JOSEException {
        if (request == null) throw new AppException(AppCode.UNAUTHENTICATED);
        String rt = request.refreshToken();

        SignedJWT token = jwtService.verifyToken(rt, "refresh");
        jwtService.invalidatedToken(token);
    }
}
