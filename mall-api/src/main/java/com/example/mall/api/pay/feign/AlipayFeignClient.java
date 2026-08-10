package com.example.mall.api.pay.feign;

import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mall-pay-service", contextId = "alipay-feign")
public interface AlipayFeignClient {

    @GetMapping("/alipay/payment/query/{orderId}")
    Result queryPaymentResult(@PathVariable("orderId") String orderId) throws Exception;
}
