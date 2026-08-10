package com.example.mall.product.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("category_brand")
public class CategoryBrand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "category_id", type = IdType.INPUT)
    private Integer categoryId;

    /**
     * 品牌ID
     */
    private Integer brandId;
}
