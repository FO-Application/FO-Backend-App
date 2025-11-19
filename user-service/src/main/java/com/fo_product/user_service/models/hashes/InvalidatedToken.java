package com.fo_product.user_service.models.hashes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("InvalidatedToken")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidatedToken {
    @Id
    String jwtId;

    @TimeToLive(unit = TimeUnit.SECONDS)
    long expiredTime;
}
