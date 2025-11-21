package com.fo_product.user_service.services.interfaces;

import com.fo_product.user_service.resources.requests.*;
import com.fo_product.user_service.resources.responses.AuthResponse;
import com.fo_product.user_service.resources.responses.PendingUserResponse;
import com.fo_product.user_service.resources.responses.UserResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface IAuthService {
    UserResponse verifyAndCreateUser(VerifyOtpRequest request);
    PendingUserResponse createPendingUser(UserRequest request);
    void resendOtp(EmailRequest request);
    AuthResponse authentication(AuthenticateRequest request);
    AuthResponse refreshToken(TokenRequest request) throws ParseException, JOSEException;
    void logout(TokenRequest request) throws ParseException, JOSEException;
}
