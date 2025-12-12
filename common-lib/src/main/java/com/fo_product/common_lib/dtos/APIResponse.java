package com.fo_product.common_lib.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema; // Import cái này
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {

    @Builder.Default
    @Schema(description = "Mã trạng thái (1000 là thành công)", example = "1000")
    int code = 1000;

    @Schema(description = "Thông báo phản hồi", example = "Xử lý thành công")
    String message;

    @Schema(description = "Dữ liệu trả về (Kết quả chính)")
    T result;
}