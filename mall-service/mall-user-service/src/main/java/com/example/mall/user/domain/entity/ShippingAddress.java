package com.example.mall.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mall.util.EncryptHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 用户收货地址表
 * </p>
 *
 * @author yy
 * @since 2026-07-08
 */
@Getter
@Setter
@ToString
@TableName(value = "shipping_address",autoResultMap = true)
public class ShippingAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 收货人名称
     */
    @TableField("name")
    private String name;

    /**
     * 收货人电话
     */
    @TableField("phone_number")
    private String phoneNumber;

    /**
     * 是否为默认:1-默认,0-不默认
     */
    @TableField("default_status")
    private Integer defaultStatus;

    /**
     * 邮政编码
     */
    @TableField("post_code")
    private String postCode;

    /**
     * 省份/直辖市
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 区
     */
    @TableField("region")
    private String region;

    /**
     * 详细地址(街道)
     */
    @TableField(typeHandler = EncryptHandler.class)
    private String detailAddress;
}
