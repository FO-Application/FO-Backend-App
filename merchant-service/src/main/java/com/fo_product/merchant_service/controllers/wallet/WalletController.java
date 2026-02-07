package com.fo_product.merchant_service.controllers.wallet;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.responses.wallet.DailyStatResponse;
import com.fo_product.merchant_service.dtos.responses.wallet.WalletResponse;
import com.fo_product.merchant_service.dtos.responses.wallet.WalletTransactionResponse;
import com.fo_product.merchant_service.models.enums.TransactionType;
import com.fo_product.merchant_service.services.interfaces.wallet.IWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Wallet Controller", description = "Quản lý Ví tiền của Merchant")
public class WalletController {
    IWalletService walletService;

    @Operation(summary = "Xem số dư Ví", description = "Lấy thông tin ví hiện tại của Merchant đang đăng nhập")
    @GetMapping
    public APIResponse<WalletResponse> getMyWallet() {
        WalletResponse result = walletService.getMyWallet();
        return APIResponse.<WalletResponse>builder()
                .result(result)
                .message("Get wallet success")
                .build();
    }

    @Operation(summary = "Xem lịch sử giao dịch", description = "Lấy danh sách biến động số dư, hỗ trợ lọc theo ngày và loại giao dịch")
    @GetMapping("/transactions")
    public APIResponse<Page<WalletTransactionResponse>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(required = false) TransactionType type
    ) {
        Page<WalletTransactionResponse> result = walletService.getMyTransactions(page, size, startDate, endDate, type);
        return APIResponse.<Page<WalletTransactionResponse>>builder()
                .result(result)
                .message("Get transactions success")
                .build();
    }
    
    @Operation(summary = "Xuất báo cáo giao dịch", description = "Xuất lịch sử giao dịch ra file CSV")
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(required = false) TransactionType type
    ) {
        byte[] data = walletService.exportTransactions(startDate, endDate, type);
        ByteArrayResource resource = new ByteArrayResource(data);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=transactions.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }

    @Operation(summary = "Thống kê doanh thu", description = "Lấy thống kê thu chi theo ngày")
    @GetMapping("/statistics")
    public APIResponse<List<DailyStatResponse>> getDailyStatistics() {
        return APIResponse.<List<DailyStatResponse>>builder()
                .result(walletService.getDailyStatistics())
                .build();
    }

    @Operation(summary = "Rút tiền", description = "Chủ nhà hàng dùng để rút tiền")
    @PostMapping("/withdraw")
    public APIResponse<WalletResponse> withdraw(@RequestParam BigDecimal amount) {
        return APIResponse.<WalletResponse>builder()
                .result(walletService.withdraw(amount))
                .build();
    }
    
    @Operation(summary = "Nạp tiền", description = "Chủ nhà hàng dùng để nạp tiền (giả lập)")
    @PostMapping("/deposit")
    public APIResponse<WalletResponse> deposit(@RequestParam BigDecimal amount) {
        return APIResponse.<WalletResponse>builder()
                .result(walletService.deposit(amount))
                .build();
    }
}
