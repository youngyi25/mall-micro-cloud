package com.example.mall.api.category.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */

    private Integer id;

    /**
     * 分类名称
     */

    private String name;

    /**
     * 下级分类集合
     */
    private List<CategoryDTO> children;
}
