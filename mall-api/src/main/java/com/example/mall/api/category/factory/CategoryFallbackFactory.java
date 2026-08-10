package com.example.mall.api.category.factory;

import com.example.mall.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.example.mall.api.category.feign.CategoryFeignClient;
import com.example.mall.api.category.dto.CategoryDTO;

import java.util.List;

@Slf4j
@Component
public class CategoryFallbackFactory implements FallbackFactory<CategoryFeignClient> {
    // Throwable：远程调用产生的所有异常（超时、服务不存在、下游限流、宕机）
    @Override
    public CategoryFeignClient create(Throwable cause) {
        // 打印远程调用故障日志，便于排查下游问题
        log.error("调用商品服务远程接口熔断降级，异常信息：{}", cause.getMessage());
        // 创建兜底数据
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(0);
        categoryDTO.setName("未知分类");
        // 返回Feign接口的兜底实现类，必须实现所有Feign接口方法
        return new CategoryFeignClient() {
            @Override
            public Result<List<CategoryDTO>> listLevelOne() {
                // 统一兜底返回：使用本地缓存数据，不依赖下游服务
                return Result.success(List.of(categoryDTO));
            }

            @Override
            public Result<List<CategoryDTO>> listChildren(Integer parentId) {
                return Result.success(List.of(categoryDTO));
            }
        };
    }
}
