package com.example.mall.pay.domain.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付记录表
 * </p>
 *
 * @author yy
 * @since 2026-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_log")
@Schema(name = "PayLog", description = "支付记录表")
public class PayLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "支付流水号")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @Schema(description = "订单编号")
    private String orderId;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "提交时间")
    private LocalDateTime createTime;


}
