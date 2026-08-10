package com.example.mall.api.user.dto;

import com.example.mall.constant.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;

    private String username;

    private String email;

    private String nickName;

    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private UserStatusEnum status;
    /**
     * 重写获取用户状态方法
     * @return
     */
    public String getStatus() {
        return UserStatusEnum.getByCode(status.getCode()).getDesc();
    }
}
