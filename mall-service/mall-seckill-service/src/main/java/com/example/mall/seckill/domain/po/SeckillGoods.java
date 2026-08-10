package com.example.mall.seckill.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author yy
 * @since 2026-08-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("seckill_goods")
@Schema(name = "SeckillGoods", description = "秒杀商品")
public class SeckillGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "spu ID")
    private String spuId;

    @Schema(description = "sku ID")
    private String skuId;

    @Schema(description = "标题")
    private String skuName;

    @Schema(description = "商品图片")
    private String image;

    @Schema(description = "原价格")
    private Integer price;

    @Schema(description = "秒杀价格")
    private Integer secki11Price;

    @Schema(description = "秒杀商品数")
    private Integer num;

    @Schema(description = "剩余库存数")
    private Integer storeCount;

    @Schema(description = "描述")
    private String content;

    @Schema(description = "活动ID")
    private Integer activityId;


}
