package com.example.mall.pay.controller;

import com.example.mall.api.pay.model.PayCreateModel;
import com.example.mall.pay.config.AlipayConfiguration;
import com.example.mall.pay.service.IAlipayService;
import com.example.mall.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.apache.rocketmq.common.utils.DataConverter.CHARSET_UTF8;

@RestController
@RequestMapping("/alipay")
@RequiredArgsConstructor
public class AlipayController {
    private final IAlipayService alipayService;
    private final AlipayConfiguration alipayConfiguration;
    @GetMapping("/pay")
    public void pay(PayCreateModel payCreateDTO, HttpServletResponse response) throws Exception{
        String result = alipayService.pay(payCreateDTO);
        response.setContentType("text/html;charset=" + CHARSET_UTF8);
        response.getWriter().write(result);
        response.getWriter().flush();
        response.getWriter().close();
    }
    @PostMapping("/notify")
    public void payNotify(HttpServletRequest request){
        Map<String, String[]> result = request.getParameterMap();
        if("TRADE_SUCCESS".equals(request.getParameter("trade_status"))){
            alipayService.payNotify(result);
        }
    }
    @GetMapping("/payment/query/{orderId}")
    public Result queryPaymentResult(@PathVariable String orderId) throws Exception {
        String result = alipayService.paymentCallbackVerify(orderId);
        return Result.success(result);
    }
}
