package com.fo_product.user_service.services.imps;

import com.fo_product.user_service.exceptions.code.UserErrorCode;
import com.fo_product.user_service.exceptions.UserException;
import com.fo_product.user_service.models.entities.Role;
import com.fo_product.user_service.models.entities.User;
import com.fo_product.user_service.models.hashes.InvalidatedToken;
import com.fo_product.user_service.models.repositories.InvalidatedTokenRepository;
import com.fo_product.user_service.models.repositories.UserRepository;
import com.fo_product.user_service.services.interfaces.IJwtService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService implements IJwtService {
    InvalidatedTokenRepository invalidatedTokenRepository;
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.secret}")
    protected String SECRET_KEY;

    @NonFinal
    @Value("${jwt.access-token-expiration}")
    protected int ACCESS_TOKEN_EXPIRATION;

    @NonFinal
    @Value("${jwt.refresh-token-expiration}")
    protected int REFRESH_TOKEN_EXPIRATION;

    private String generateToken(User user, long expiration, String tokenType) {
        //Create header for JWT Token
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        //Create body for jwt token
        JWTClaimsSet.Builder body = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .jwtID(UUID.randomUUID().toString())
                .claim("token-type", tokenType)
                .claim("user-id", user.getId())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(expiration, ChronoUnit.SECONDS).toEpochMilli()
                ));

        //Nếu là access token thì sẽ thêm claim build scope vào
        if ("access".equals(tokenType)) {
            body.claim("scope", buildScope(user));
        }

        //Lưu lại body vào biến jwtBody
        JWTClaimsSet jwtBody = body.build();
        Payload payload = new Payload(jwtBody.toJSONObject());

        //Nhét header và payload vào trong jwsObject, chuẩn bị kí token
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new UserException(UserErrorCode.CREATE_TOKEN_ERROR);
        }
    }

    @Override
    public SignedJWT verifyToken(String token, String type) throws JOSEException, ParseException {
        if (token == null || token.trim().isEmpty()) {
            throw new UserException(UserErrorCode.UNAUTHENTICATED);
        }

        //Kí với chữ kí bảo mật của hệ thống
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        //Trả về boolean true/false token đã xác thực
        boolean verified = signedJWT.verify(verifier);

        //Token không hợp lệ => invalid
        if (!verified) {
            throw new UserException(UserErrorCode.UNAUTHENTICATED);
        }

        //Kiểm tra ngày hạn có nằm sau bây giờ không
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiredTime.before(new Date())) {
            throw new UserException(UserErrorCode.UNAUTHENTICATED);
        }

        //Kiểm tra thể loại token có hợp lệ không
        String tokenType = signedJWT.getJWTClaimsSet().getStringClaim("token-type");
        if (!type.equals(tokenType)) {
            throw new UserException(UserErrorCode.UNAUTHENTICATED);
        }

        //Kiểm tra token này đã bị đánh dấu vào black list chưa
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if (invalidatedTokenRepository.existsById(jwtId)) {
            throw new UserException(UserErrorCode.UNAUTHENTICATED);
        }

        //Trả về token nếu hợp lệ, không thì sẽ bị ném ngoại lệ
        return signedJWT;
    }

    @Override
    public Map<String, Object> refreshToken(String token) throws ParseException, JOSEException {
        SignedJWT jwt = verifyToken(token, "refresh");
        invalidatedToken(jwt);

        String email = jwt.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_EXIST));

        String role = user.getRole().getName();
        TokenPair tokenPair = generateTokenPair(user);

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", tokenPair.getAccessToken());
        result.put("refreshToken", tokenPair.getRefreshToken());
        result.put("role", role);

        return result;
    }

    @Override
    public void invalidatedToken(SignedJWT signedJWT) throws ParseException {
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();


        long timeLeftBySeconds = (expirationTime.getTime() - System.currentTimeMillis()) / 1000;

        log.info("Time left: {}", timeLeftBySeconds);
        if (timeLeftBySeconds <= 0) {
            log.info("Token đã hết hạn, không cần add vào black list");
            return;
        }

        InvalidatedToken invalidatedToken = new InvalidatedToken();
        invalidatedToken.setJwtId(jwtId);
        invalidatedToken.setExpiredTime(timeLeftBySeconds);

        invalidatedTokenRepository.save(invalidatedToken);
        log.info("Đã gọi lệnh Save vào Redis với ID: {}", jwtId);
    }

    //Helper method
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        Role role = user.getRole();
        if (role != null) {
            stringJoiner.add("ROLE_" + role.getName());
            if (!CollectionUtils.isEmpty(role.getPermissions())) {
                role.getPermissions()
                        .forEach(permission -> stringJoiner.add(permission.getName()));
            }
        }
        return stringJoiner.toString();
    }

    @Override
    public TokenPair generateTokenPair(User user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        TokenPair tokenPair = new TokenPair();
        tokenPair.setAccessToken(accessToken);
        tokenPair.setRefreshToken(refreshToken);

        return tokenPair;
    }

    private String generateAccessToken(User user) {
        return generateToken(user, ACCESS_TOKEN_EXPIRATION, "access");
    }

    private String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_TOKEN_EXPIRATION, "refresh");
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TokenPair {
        String accessToken;
        String refreshToken;
    }
}
