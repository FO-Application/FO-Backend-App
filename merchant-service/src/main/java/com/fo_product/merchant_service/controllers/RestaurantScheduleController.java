package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantSchedulePatchRequest;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantScheduleRequest;
import com.fo_product.merchant_service.dtos.responses.RestaurantScheduleResponse;
import com.fo_product.merchant_service.services.interfaces.IRestaurantScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurant-schedule")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Restaurant Schedule", description = "Quản lý lịch mở cửa/đóng cửa của nhà hàng")
public class RestaurantScheduleController {
    IRestaurantScheduleService restaurantScheduleService;

    @Operation(summary = "Tạo lịch hoạt động", description = "Thêm khung giờ mở cửa cho một ngày cụ thể.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi validate (Giờ mở > Giờ đóng hoặc trùng lịch)")
    })
    @PostMapping
    APIResponse<RestaurantScheduleResponse> createRestaurantSchedule(@Valid @RequestBody RestaurantScheduleRequest request){
        RestaurantScheduleResponse result = restaurantScheduleService.createRestaurantSchedule(request);
        return APIResponse.<RestaurantScheduleResponse>builder()
                .result(result)
                .message("Create restaurant schedule successfully")
                .build();
    }

    @Operation(summary = "Cập nhật lịch", description = "Sửa đổi giờ mở/đóng cửa.")
    @PutMapping("/{scheduleId}")
    APIResponse<RestaurantScheduleResponse> updateRestaurantSchedule(
            @Parameter(description = "ID của lịch trình", example = "1")
            @PathVariable("scheduleId") Long id, // Đã sửa lỗi typo "shceduleId" ở đây
            @RequestBody RestaurantSchedulePatchRequest request
    ) {
        RestaurantScheduleResponse result = restaurantScheduleService.updateRestaurantScheduleById(id, request);
        return APIResponse.<RestaurantScheduleResponse>builder()
                .result(result)
                .message("Update restaurant schedule successfully")
                .build();
    }

    @Operation(summary = "Xem chi tiết lịch", description = "Lấy thông tin một lịch trình cụ thể.")
    @GetMapping("/{scheduleId}")
    APIResponse<RestaurantScheduleResponse> getRestaurantSchedule(
            @Parameter(description = "ID của lịch trình", example = "1")
            @PathVariable("scheduleId") Long id
    ) {
        RestaurantScheduleResponse result = restaurantScheduleService.getRestaurantScheduleById(id);
        return APIResponse.<RestaurantScheduleResponse>builder()
                .result(result)
                .message("Get restaurant schedule successfully")
                .build();
    }

    @Operation(summary = "Lấy tất cả lịch", description = "Lấy danh sách toàn bộ lịch hoạt động (thường dùng cho Admin hoặc debug).")
    @GetMapping
    APIResponse<List<RestaurantScheduleResponse>> getAllRestaurantSchedules(){
        List<RestaurantScheduleResponse> result = restaurantScheduleService.getAllRestaurantSchedules();
        return APIResponse.<List<RestaurantScheduleResponse>>builder()
                .result(result)
                .message("Get all restaurant schedules")
                .build();
    }

    @Operation(summary = "Xóa lịch", description = "Xóa một khung giờ hoạt động.")
    @DeleteMapping("/{scheduleId}")
    APIResponse<?> deleteRestaurantSchedule(
            @Parameter(description = "ID của lịch trình", example = "1")
            @PathVariable("scheduleId") Long id
    ){
        restaurantScheduleService.deleteRestaurantScheduleById(id);
        return APIResponse.builder()
                .result("Delete restaurant schedule successfully") // Lưu ý: result thường trả về Object, message trả về String
                .message("Delete successfully")
                .build();
    }
}