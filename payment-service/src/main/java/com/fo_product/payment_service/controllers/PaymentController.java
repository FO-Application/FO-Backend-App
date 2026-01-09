package com.fo_product.payment_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.payment_service.services.imps.ZaloPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final ZaloPayService zaloPayService;

    @PostMapping("/zalopay/create")
    public APIResponse<Map<String, Object>> createPayment(
            @RequestParam Long orderId,
            @RequestParam long amount
    ) {
        try {
            Map<String, Object> result = zaloPayService.createOrder(orderId, amount);
            return APIResponse.<Map<String, Object>>builder()
                    .result(result)
                    .message("Tạo đơn ZaloPay thành công")
                    .build();
        } catch (Exception e) {
            log.error("Lỗi tạo đơn ZaloPay: ", e);
            return APIResponse.<Map<String, Object>>builder()
                    .code(500)
                    .message("Lỗi: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/callback")
    public String callback(@RequestBody String jsonStr) {
        log.info("ZaloPay Callback: {}", jsonStr);
        // Trả về JSON này để ZaloPay biết mình đã nhận tin
        return "{\"return_code\": 1, \"return_message\": \"success\"}";
    }
}