package com.example.mall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "订单查询参数")
@Data
public class OrderQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户编号")
    private Long userId;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "当前页")
    private Integer pageNum;

    @Schema(description = "每页数量")
    private Integer pageSize;
}
