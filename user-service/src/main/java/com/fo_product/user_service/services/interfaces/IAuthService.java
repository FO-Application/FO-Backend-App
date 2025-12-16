package com.fo_product.user_service.services.interfaces;

import com.fo_product.user_service.dtos.requests.*;
import com.fo_product.user_service.dtos.responses.AuthenticationDTO;
import com.fo_product.user_service.dtos.responses.PendingUserResponse;
import com.fo_product.user_service.dtos.responses.UserResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface IAuthService {
    PendingUserResponse createPendingUser(UserRequest request, String role);
    UserResponse verifyAndCreateUser(VerifyOtpRequest request);
    void resendOtp(EmailRequest request);
    AuthenticationDTO authentication(AuthenticateRequest request);
    AuthenticationDTO refreshToken(String refreshToken) throws ParseException, JOSEException;
    void logout(String refreshToken) throws ParseException, JOSEException;
}
