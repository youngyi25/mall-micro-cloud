package com.example.mall.exception;

import com.example.mall.constant.ResultCodeEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final Integer code;

    public BusinessException(ResultCodeEnum codeEnum) {
        super(codeEnum.getMsg());
        this.code = codeEnum.getCode();
    }
    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
