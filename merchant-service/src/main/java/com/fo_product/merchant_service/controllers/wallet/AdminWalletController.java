package com.fo_product.merchant_service.controllers.wallet;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.responses.wallet.WalletTransactionResponse;
import com.fo_product.merchant_service.models.enums.TransactionType;
import com.fo_product.merchant_service.services.interfaces.wallet.IAdminWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/management/wallet/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Admin Wallet Controller", description = "Quản lý toàn bộ giao dịch thống kê hệ thống dành cho Super Admin")
public class AdminWalletController {
    IAdminWalletService adminWalletService;

    @Operation(summary = "Lấy danh sách tất cả các giao dịch hệ thống", description = "Dành cho Dashboard Super Admin")
    @GetMapping("/transactions")
    public APIResponse<Page<WalletTransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(required = false) TransactionType type
    ) {
        Page<WalletTransactionResponse> result = adminWalletService.getAllTransactions(page, size, startDate, endDate, type);
        return APIResponse.<Page<WalletTransactionResponse>>builder()
                .result(result)
                .message("Get all transactions successfully")
                .build();
    }
}
