package com.fo_product.user_service.services.interfaces;

import com.fo_product.user_service.models.entities.User;
import com.fo_product.user_service.services.imps.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.util.Map;

public interface IJwtService {
    JwtService.TokenPair generateTokenPair(User user);
    SignedJWT verifyToken(String token, String type) throws JOSEException, ParseException;
    Map<String, Object> refreshToken(String token) throws ParseException, JOSEException;
    void invalidatedToken(SignedJWT signedJWT) throws ParseException;
}
