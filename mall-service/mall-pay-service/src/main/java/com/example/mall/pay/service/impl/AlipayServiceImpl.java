package com.example.mall.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.example.mall.api.pay.model.PayCreateModel;
import com.example.mall.pay.config.AlipayConfiguration;
import com.example.mall.pay.domain.po.PayLog;
import com.example.mall.pay.service.IAlipayService;
import com.example.mall.pay.service.IMessageSendService;
import com.example.mall.pay.service.IPayLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AlipayServiceImpl implements IAlipayService {
    private final AlipayConfiguration alipayConfiguration;
    private final IPayLogService payLogService;
    private final IMessageSendService messageSendService;
    private final StreamBridge streamBridge;
    private final RedisTemplate redisTemplate;
    @Override
    public String pay(PayCreateModel payCreateDTO) throws Exception {
        DefaultAlipayClient alipayClient = new DefaultAlipayClient(alipayConfiguration.getAlipayConfig());
        //发送支付请求
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        alipayTradePagePayRequest.setNotifyUrl(alipayConfiguration.getNotifyUrl());
        alipayTradePagePayRequest.setReturnUrl(alipayConfiguration.getReturnUrl());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", payCreateDTO.getOrderId());
        jsonObject.put("subject", payCreateDTO.getSubject());
        jsonObject.put("total_amount", payCreateDTO.getTotalAmount());
        jsonObject.put("body", payCreateDTO.getBody());
        jsonObject.put("product_code", "FAST_INSTANT_TRADE_PAY");
        alipayTradePagePayRequest.setBizContent(jsonObject.toJSONString());
        String result = alipayClient.pageExecute(alipayTradePagePayRequest).getBody();

        return result;
    }

    @Override
    public void payNotify(Map<String, String[]> result) {
        String no = result.get("trade_no")[0];
        String orderId = result.get("out_trade_no")[0];
        String trade_no = result.get("total_amount")[0];
        PayLog payLog = new PayLog();
        payLog.setId(no);
        payLog.setOrderId(orderId);
        payLog.setAmount(new BigDecimal(trade_no));
        payLogService.save(payLog);
        // 发送消息，修改订单状态
        messageSendService.sendUpdateStatusMsg(orderId);
    }

    @Override
    public String paymentCallbackVerify(String outTradeNo) throws Exception {
        // 已存在支付记录，幂等返回成功
        if (payLogService.getByOrderId(outTradeNo) != null) {
            return "success";
        }
        // 查询支付宝交易状态
        DefaultAlipayClient alipayClient = new DefaultAlipayClient(alipayConfiguration.getAlipayConfig());
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(outTradeNo);
        request.setBizModel(model);
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            PayLog payLog = new PayLog();
            payLog.setId(response.getTradeNo());
            payLog.setOrderId(outTradeNo);
            payLog.setAmount(new BigDecimal(response.getTotalAmount()));
            payLogService.save(payLog);
            return "success";
        } else {
            return "fail";
        }
    }
}
