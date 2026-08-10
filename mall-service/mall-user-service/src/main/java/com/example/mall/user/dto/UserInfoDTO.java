package com.example.mall.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer status;
    /** 登录成功时由 LoginController 写入，用于前端 sessionStorage */
    private String token;
}