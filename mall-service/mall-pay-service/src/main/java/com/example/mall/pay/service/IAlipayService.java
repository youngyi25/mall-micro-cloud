package com.example.mall.pay.service;

import com.example.mall.api.pay.model.PayCreateModel;

import java.util.Map;

public interface IAlipayService {
    /**
     * 生成支付二维码
     * @param payCreateDTO 支付订单信息
     * @return
     * @throws RuntimeException
     */
    String pay(PayCreateModel payCreateDTO) throws Exception;

    /**
     * 支付回调处理
     * @param result
     */
    void payNotify(Map<String, String[]> result);
    /**
     * 查询订单状态
     * @param outTradeNo 订单号
     * @return
     * @throws Exception
     */
    String paymentCallbackVerify(String outTradeNo) throws Exception;
}
