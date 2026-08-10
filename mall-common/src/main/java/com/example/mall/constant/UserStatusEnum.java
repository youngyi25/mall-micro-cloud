package com.example.mall.constant;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
    NORMAL(0, "正常"),
    DISABLE(1, "禁用"),
    LOCK(2, "锁定");

    private final Integer code;
    private final String desc;

    UserStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 数字反向匹配枚举
    public static UserStatusEnum getByCode(Integer code) {
        for (UserStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
