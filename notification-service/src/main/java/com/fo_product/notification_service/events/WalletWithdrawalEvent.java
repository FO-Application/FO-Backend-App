package com.fo_product.notification_service.events;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletWithdrawalEvent {
    Long userId;
    BigDecimal amount;
    Long transactionId;
    Instant time;
}
