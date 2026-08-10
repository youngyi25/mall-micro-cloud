package com.example.mall.api.user.dto;

import com.example.mall.util.DesensitizeUtil;
import lombok.Data;

import java.io.Serializable;
@Data
public class ShippingAddressDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String userId;

    private String name;

    private String phoneNumber;

    private Integer defaultStatus;

    private String postCode;

    private String province;

    private String city;

    private String region;

    private String detailAddress;

    // 获取收件地址
    private String shippingAddress;
    /**
     * 获取手机号码
     *
     * @return 脱敏后的手机号
     */
    public String getPhoneNumber() {
        return DesensitizeUtil.phone(phoneNumber);
    }
    /**
     * 获取收件地址
     *
     * @return 收件地址
     */
    public String getShippingAddress() {
        return province + city + region + detailAddress;
    }
}
