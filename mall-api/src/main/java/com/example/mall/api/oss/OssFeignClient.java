package com.example.mall.api.oss;

import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mall-oss-service",contextId = "oss-feign")
public interface OssFeignClient {
    @PostMapping("/oss/uploadByte")
    Result<String> uploadByte(
            @RequestBody byte[] fileBytes,
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType,
            @RequestParam("dir") String dir
    );
}
