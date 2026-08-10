package com.example.mall.product.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 品牌表
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Getter
@Setter
@TableName("brand")
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 品牌名称
     */
    @TableField("name")
    private String name;

    /**
     * 品牌logo
     */
    @TableField("logo")
    private String logo;

    /**
     * 品牌图片地址
     */
    @TableField("image")
    private String image;

    /**
     * 品牌的首字母
     */
    @TableField("initial")
    private String initial;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;
}
