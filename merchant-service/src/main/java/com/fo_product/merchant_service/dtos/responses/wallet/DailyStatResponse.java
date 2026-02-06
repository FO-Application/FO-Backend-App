package com.fo_product.merchant_service.dtos.responses.wallet;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyStatResponse {
    LocalDate date;
    BigDecimal income;
    BigDecimal expense;
}
