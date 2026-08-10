package com.example.mall.api.order.feign;

import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mall-order-service", contextId = "order-feign")
public interface OrderFeignClient {

    @DeleteMapping("/order/remove/{orderId}")
    Result removeById(@PathVariable("orderId") String orderId);
}
