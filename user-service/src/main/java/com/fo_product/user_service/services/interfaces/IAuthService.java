package com.fo_product.user_service.services.interfaces;

import com.fo_product.user_service.resources.requests.AuthenticateRequest;
import com.fo_product.user_service.resources.requests.TokenRequest;
import com.fo_product.user_service.resources.responses.AuthResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface IAuthService {
    AuthResponse authentication(AuthenticateRequest request);
    AuthResponse refreshToken(TokenRequest request) throws ParseException, JOSEException;
    void logout(TokenRequest request) throws ParseException, JOSEException;
}
