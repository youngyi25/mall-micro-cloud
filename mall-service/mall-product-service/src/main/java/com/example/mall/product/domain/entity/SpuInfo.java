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
 * 
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Getter
@Setter
@TableName("spu_info")
public class SpuInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * spuid
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * spu名称
     */
    @TableField("spu_name")
    private String spuName;

    /**
     * 商品描述
     */
    @TableField("description")
    private String description;
}
