package com.fo_product.user_service.models.hashes;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("PendingUser")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PendingUser {
    @Id
    String email;

    String password;
    String firstName;
    String lastName;
    String phone;
    LocalDate dob;

    @TimeToLive(unit = TimeUnit.MINUTES)
    Long expiredTime;
}
