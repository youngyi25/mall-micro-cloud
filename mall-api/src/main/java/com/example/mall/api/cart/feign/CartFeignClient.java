package com.example.mall.api.cart.feign;

import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        contextId = "cart-feign",
        name = "mall-cart-service"
)
public interface CartFeignClient {

    @DeleteMapping("/cart/deleteBatch")
    Result removeCart(@RequestBody List<String> ids);
}
