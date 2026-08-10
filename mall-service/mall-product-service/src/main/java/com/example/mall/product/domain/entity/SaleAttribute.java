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
@TableName("sale_attribute")
public class SaleAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * spuid
     */
    @TableField("spu_id")
    private Long spuId;

    /**
     * 属性id
     */
    @TableField("base_sale_attr_id")
    private Long baseSaleAttrId;

    /**
     * 属性名称
     */
    @TableField("sale_attr_name")
    private String saleAttrName;
}
