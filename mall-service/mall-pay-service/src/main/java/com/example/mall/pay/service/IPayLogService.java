package com.example.mall.pay.service;

import com.example.mall.pay.domain.po.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 支付记录表 服务类
 * </p>
 *
 * @author yy
 * @since 2026-08-04
 */
public interface IPayLogService extends IService<PayLog> {

    /**
     * 按订单查询交易记录
     * @param orderId
     * @return
     */
    PayLog getByOrderId(String orderId);
}
