package com.example.mall.test.controller;

import com.example.mall.test.domain.dto.UserDTO;
import com.example.mall.util.JsonUtils;
import com.example.mall.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test/redis")
@RequiredArgsConstructor
public class RedisContorller {
    private  final RedisTemplate<String,Object> redisTemplate;
    /**
     * 使用 opsForValue 存储字符串
     * 特点：每次操作都需要指定完整的 Key
     */
    @GetMapping("/string/opsFor")
    public Result<String> stringExampleWithOpsFor(String key,String value){
        redisTemplate.opsForValue().set(key,"refresh:"+value,30, TimeUnit.MINUTES);
        String token = (String)redisTemplate.opsForValue().get(key);
        return Result.success(token);
    }
    @GetMapping("/string/bound")
    public Result<String> stringExampleWithBound(String key,String value){
        BoundValueOperations<String, Object> boundOps = redisTemplate.boundValueOps(key);
        boundOps.set("refresh:"+value,30, TimeUnit.MINUTES);
        String token = (String)boundOps.get();
        return Result.success(token);
    }
    @PostMapping("/hashSet/save")
    public Result save(@RequestBody UserDTO userDTO) {
        String key = "user:" + userDTO.getId();
        // 使用 Hash 存储对象属性，方便单独更新某个字段
        Map<String, Object> map = new HashMap<>();
        map.put("name", userDTO.getName());
        map.put("age", userDTO.getAge());
        map.put("email", userDTO.getEmail());
        redisTemplate.opsForHash().putAll(key, map);
        // 设置整个 Hash 的过期时间
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
        return Result.success();
    }
    @GetMapping("/hashSet/get")
    public UserDTO get(String key) {
        String userKey = "user:" + key;
        // 获取整个 Hash 的所有字段和值
        Map<Object, Object> map = redisTemplate.opsForHash().entries(userKey);
        map.put("id", key);
        return JsonUtils.toObj(JsonUtils.toJson(map),UserDTO.class);
    }
}
