package com.fo_product.user_service.models.hashes;

import com.fo_product.user_service.models.enums.OtpTokenType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("OtpToken")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpToken {
    @Id
    String email;

    String otpCode;

    @Enumerated(EnumType.STRING)
    OtpTokenType type;

    @TimeToLive(unit = TimeUnit.MINUTES)
    Long expiredTime;
}
