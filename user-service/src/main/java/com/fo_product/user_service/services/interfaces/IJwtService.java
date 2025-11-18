package com.fo_product.user_service.services.interfaces;

import com.fo_product.user_service.models.entities.User;
import com.fo_product.user_service.services.imps.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public interface IJwtService {
    JwtService.TokenPair generateTokenPair(User user);
    SignedJWT verifyToken(String token, String type) throws JOSEException, ParseException;
    JwtService.TokenPair refreshToken(String token) throws ParseException, JOSEException;
    void invalidatedToken(SignedJWT signedJWT) throws ParseException;
}
