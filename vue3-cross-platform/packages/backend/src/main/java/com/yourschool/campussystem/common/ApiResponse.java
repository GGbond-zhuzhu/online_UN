package com.yourschool.campussystem.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;  // 【添加这行】解决List导入问题

@Data
@Accessors(chain = true)
@Schema(description = "统一API响应格式")
public class ApiResponse<T> {

    @Schema(description = "状态码", example = "200")
    private Integer code;

    @Schema(description = "提示信息", example = "success")
    private String msg;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "请求时间戳", example = "1700000000000")
    private Long timestamp = System.currentTimeMillis();

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<T>()
                .setCode(200)
                .setMsg("success");
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>()
                .setCode(200)
                .setMsg("success")
                .setData(data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>()
                .setCode(200)
                .setMsg(message)
                .setData(data);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<T>()
                .setCode(code)
                .setMsg(message);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<T>()
                .setCode(errorCode.getCode())
                .setMsg(errorCode.getMsg());
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return new ApiResponse<T>()
                .setCode(errorCode.getCode())
                .setMsg(message);
    }

    // 分页响应
    public static <T> ApiResponse<PageResult<T>> page(List<T> list, Integer page, Integer size, Long total) {
        PageResult<T> pageResult = PageResult.of(list, page, size, total);
        return success(pageResult);
    }
}