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
@TableName("sale_attribute_value")
public class SaleAttributeValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 销售属性值编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品id
     */
    @TableField("spu_id")
    private Long spuId;

    /**
     * 销售属性id
     */
    @TableField("base_sale_attr_id")
    private Long baseSaleAttrId;

    /**
     * 销售属性值名称
     */
    @TableField("sale_attr_value_name")
    private String saleAttrValueName;

    /**
     * 销售属性名称
     */
    @TableField("sale_attr_name")
    private String saleAttrName;
}
