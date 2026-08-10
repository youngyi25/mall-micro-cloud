package com.example.mall.constant;

import lombok.Getter;

/**
 * 全局接口响应码枚举
 * 【编码规则】
 * 1. 1~999：HTTP标准状态码
 *    200=成功；4xx=客户端错误；5xx=服务通用异常
 * 2. 4000~4999：用户、登录、Token、权限相关业务异常
 * 3. 5000~5999：业务数据(库存、订单等)自定义异常
 */
@Getter
public enum ResultCodeEnum {
    // HTTP标准码段 1~999
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "请求参数校验失败"),
    UNAUTHORIZED(401, "未登录，请先登录"),
    FORBIDDEN(403, "访问受限，权限不足"),
    NOT_FOUND(404, "请求资源不存在"),
    FAIL(500, "服务器内部异常"),

    // 用户&登录&权限 4000~4999
    TOKEN_EXPIRED(4001, "登录令牌已过期，请重新登录"),
    TOKEN_INVALID(4002, "非法token，认证失败"),
    ACCOUNT_DISABLED(4003, "账号不存在或已禁用，无法登录"),
    PASSWORD_ERR(4004, "密码错误"),
    USER_EXIST(4005, "该账号已存在"),
    LOGIN_STATUS_EXPIRE(4006, "登录会话已失效，请重新登录"),

    // 业务数据异常 5000~5999
    STOCK_SHORTAGE(5001, "商品库存不足");


    private final Integer code;
    private final String msg;

    ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
