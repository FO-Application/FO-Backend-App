package com.fo_product.payment_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.payment_service.services.interfaces.IZaloPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Controller", description = "Quản lý thanh toán ZaloPay (Tạo đơn, Callback, Truy vấn)")
public class PaymentController {
    private final IZaloPayService zaloPayService;

    @Operation(
            summary = "Tạo link thanh toán ZaloPay",
            description = "Trả về order_url (link thanh toán) và mã giao dịch. Client dùng link này để hiển thị QR Code hoặc mở app ZaloPay."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo đơn thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": 1000, \"result\": {\"order_url\": \"https://zalopay...\", \"return_code\": 1}}"))),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống hoặc lỗi từ ZaloPay")
    })
    @PostMapping("/zalopay/create")
    public APIResponse<Map<String, Object>> createPayment(
            @Parameter(description = "ID đơn hàng từ hệ thống Order Service", example = "101")
            @RequestParam Long orderId,

            @Parameter(description = "Số tiền cần thanh toán (VND)", example = "50000")
            @RequestParam long amount
    ) throws Exception {
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

    @Operation(
            summary = "Webhook Callback (Dành riêng cho ZaloPay Server)",
            description = "LƯU Ý: Frontend/Mobile KHÔNG gọi API này. Đây là API để Server ZaloPay tự động gọi về khi khách thanh toán thành công."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xử lý callback thành công, trả về JSON cho ZaloPay",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"return_code\": 1, \"return_message\": \"success\"}")))
    })
    @PostMapping("/callback")
    public ResponseEntity<String> callback(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Chuỗi JSON raw data từ ZaloPay gửi sang",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"data\": \"...encrypted...\", \"mac\": \"...hash...\"}"))
            )
            @RequestBody String jsonStr
    ) throws Exception {
        log.info("ZaloPay Callback: {}", jsonStr);
        JSONObject result = new JSONObject();
        return new ResponseEntity<>(this.zaloPayService.callBack(result, jsonStr).toString(), HttpStatus.OK);
    }

    @Operation(
            summary = "Truy vấn trạng thái đơn hàng (Query Status)",
            description = "Chủ động hỏi ZaloPay xem đơn hàng đã thanh toán chưa. Dùng khi Client không nhận được kết quả hoặc dùng cho Cron Job check sót đơn."
    )
    @PostMapping("/zalopay/query")
    public APIResponse<Map<String, Object>> queryPayment(
            @Parameter(description = "Mã giao dịch ứng dụng (app_trans_id) nhận được lúc tạo đơn", example = "250110_123456")
            @RequestParam String appTransId
    ) throws Exception {
        try {
            Map<String, Object> result = zaloPayService.queryOrder(appTransId);
            return APIResponse.<Map<String, Object>>builder()
                    .result(result)
                    .message("Truy vấn trạng thái thành công")
                    .build();
        } catch (Exception e) {
            return APIResponse.<Map<String, Object>>builder()
                    .code(500)
                    .message(e.getMessage())
                    .build();
        }
    }
}