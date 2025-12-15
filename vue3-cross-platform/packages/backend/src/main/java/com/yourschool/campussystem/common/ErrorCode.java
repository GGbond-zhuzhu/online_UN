package com.yourschool.campussystem.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "错误码枚举")
public enum ErrorCode {

    // 成功
    SUCCESS(200, "成功"),

    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),

    // 业务错误码 1000-1999
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_EXIST(1002, "用户已存在"),
    USERNAME_EXIST(1002, "用户名已存在"),  // 与USER_EXIST同义，保持兼容
    USER_PASSWORD_ERROR(1003, "用户名或密码错误"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),  // 与USER_PASSWORD_ERROR同义
    USER_DISABLED(1004, "用户已被禁用"),
    ACCOUNT_LOCKED(1004, "账号已被禁用"),  // 与USER_DISABLED同义
    USER_NOT_LOGIN(1005, "用户未登录"),
    NOT_LOGIN(1005, "用户未登录"),  // 与USER_NOT_LOGIN同义
    PERMISSION_DENIED(1006, "权限不足"),
    USER_ROLE_ERROR(1007, "用户角色错误"),
    PASSWORD_MISMATCH(1008, "两次输入的密码不一致"),

    // Token错误 2000-2009
    TOKEN_INVALID(2001, "Token无效"),
    TOKEN_EXPIRED(2002, "Token过期"),
    
    // 校园卡错误 2010-2099
    ECARD_NOT_EXIST(2010, "校园卡不存在"),
    ECARD_BALANCE_INSUFFICIENT(2011, "校园卡余额不足"),
    ECARD_BALANCE_NOT_ENOUGH(2011, "校园卡余额不足"),  // 同义
    ECARD_STATUS_ERROR(2012, "校园卡状态异常"),
    ECARD_LOCATION_ERROR(2013, "位置不在校内，无法使用"),
    ECARD_VISITOR_EXPIRED(2014, "游客卡已过期"),

    // 二手交易错误 2100-2199
    GOODS_NOT_EXIST(2101, "商品不存在"),
    GOODS_STATUS_ERROR(2102, "商品状态异常"),
    GOODS_OPERATION_DENIED(2103, "无权操作此商品"),
    GOODS_ALREADY_SOLD(2104, "商品已售出"),
    GOODS_IMAGE_ERROR(2105, "图片格式错误"),

    // 兼职错误 2200-2299
    PARTTIME_NOT_EXIST(2201, "兼职不存在"),
    PARTTIME_STATUS_ERROR(2202, "兼职状态异常"),
    PARTTIME_APPLY_DUPLICATE(2203, "已申请过此兼职"),
    PARTTIME_NOT_SAME_SCHOOL(2204, "非同校，无法申请"),
    PARTTIME_RECRUIT_FULL(2205, "招聘人数已满"),

    // 行程管理错误 2300-2399
    SCHEDULE_NOT_EXIST(2301, "行程不存在"),
    TEAM_NOT_EXIST(2302, "团队不存在"),
    TEAM_MEMBER_EXIST(2303, "已是团队成员"),
    TEAM_MEMBER_NOT_EXIST(2304, "不是团队成员"),
    TEAM_ROLE_DENIED(2305, "无权操作"),
    SCHEDULE_TIME_CONFLICT(2306, "时间冲突"),

    // 系统错误
    SYSTEM_ERROR(9999, "系统繁忙，请稍后重试");

    private final Integer code;
    private final String msg;

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}