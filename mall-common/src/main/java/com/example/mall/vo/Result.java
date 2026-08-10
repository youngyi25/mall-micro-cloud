package com.example.mall.vo;

import com.example.mall.constant.ResultCodeEnum;
import lombok.Data;

@Data

public class Result<T>{
    private Integer code;
    private String msg;
    private T data;

    /**
     * 构建统一的响应结果对象
     *
     * <p>这是一个私有的静态工厂方法，用于创建和初始化Result对象，
     * 封装了响应码、响应消息和响应数据的组装逻辑。</p>
     *
     * @param <T> 数据类型泛型参数，支持任意类型的响应数据
     * @param code 响应状态码，用于标识请求处理结果（如200表示成功，500表示失败等）
     * @param msg 响应消息，用于描述请求处理结果的详细信息
     * @param data 响应数据，承载具体的业务数据，可为null
     * @return Result<T> 封装好的统一响应结果对象，包含状态码、消息和数据
     */
    private static <T> Result<T> build(Integer code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // 成功重载方法
    public static <T> Result<T> success() {
        return build(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), null);
    }
    public static <T> Result<T> success(T data) {
        return build(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), data);
    }
    // 失败重载
    public static <T> Result<T> fail(Integer code, String msg) {
        return build(code, msg, null);
    }
    public static <T> Result<T> fail(ResultCodeEnum enums) {
        return build(enums.getCode(), enums.getMsg(), null);
    }
}
