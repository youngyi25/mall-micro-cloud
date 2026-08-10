package com.example.mall.order.domain.po;

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
 * 订单中所包含的商品
 * </p>
 *
 * @author yy
 * @since 2026-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_items")
@Schema(name = "OrderItems对象", description = "订单中所包含的商品")
public class OrderItems implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "订单id")
    private String orderId;

    @Schema(description = "销售价格")
    private Double price;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "商品SKU_ID")
    private Long skuId;

    @Schema(description = "商品名称")
    private String skuName;

    @Schema(description = "图片地址")
    private String image;

    @Schema(description = "金额合计")
    private Double amount;


}
