package com.example.mall.api.order.feign;

import com.example.mall.api.order.dto.OrderInfoUpdateDTO;
import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mall-order-service", contextId = "order-info-feign")
public interface OrderInfoFeignClient {

    @PutMapping("/orderInfo/update")
    Result update(@RequestBody OrderInfoUpdateDTO orderInfoUpdateDTO);

    @GetMapping("/orderInfo/getOrderStatus/{orderId}")
    Result<Integer> getOrderStatus(@PathVariable("orderId") String orderId);
}
