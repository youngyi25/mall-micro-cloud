package com.example.mall.es.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document( indexName = "sku_info", createIndex = true)
public class SkuInfo {
    @Id
    @Field( type = FieldType. Keyword)
    private String id;
    @Field ( name = "spu_id", type = FieldType.Long)
    private Long spuId;
    @Field ( type = FieldType.Double)
    private Long price;
    @Field( name = "sku_name", type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String skuName;
    @Field( name = "sku_attribute" ,type = FieldType.Text)
    private String skuAttribute;
    @Field( type = FieldType.Integer)
    private Integer num;
    @Field( name = "brand_id" ,type = FieldType.Long)
    private Long brandId;
    @Field( name = "brand_name" ,type = FieldType.Keyword)
    private String brandName;
    @Field( name = "category_id" ,type = FieldType.Long)
    private Long categoryId;
    @Field( name = "category_name" ,type = FieldType.Keyword)
    private String categoryName;
    @Field( name = "sku_default_img" ,type = FieldType.Keyword)
    private String skuDefaultImg;
    @Field( type = FieldType.Keyword)
    private String images;
    @Field( type = FieldType.Integer)
    private Integer status;
    @Field( name = "create_time" ,type = FieldType.Keyword)
    private String createTime;
    @Field( name = "update_time" ,type = FieldType.Keyword)
    private String updateTime;
    @Field( name = " html_url " ,type = FieldType.Keyword)
    private String htmlUrl;
}
