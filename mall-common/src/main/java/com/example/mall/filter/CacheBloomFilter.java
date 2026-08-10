package com.example.mall.filter;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 缓存布隆过滤器 — 用于防止缓存穿透。
 *
 * 原理：在缓存层前面加一层布隆过滤器，请求到来时先判断元素是否可能存在：
 * - false = 一定不存在，直接拦截（避免查 DB）
 * - true  = 可能存在，继续查缓存/DB
 *
 * 使用场景：秒杀商品 ID 校验、用户 ID 校验等高频查询且存在大量无效 key 的场景。
 *
 * 配置示例（application.yml）：
 *   cache:
 *     bloom:
 *       name: seckillGoods
 *       expectedInsertions: 100000
 *       falseProbability: 0.01
 *
 * @param <T> 元素类型
 */
@Slf4j
@Component
public class CacheBloomFilter<T> {

    private static final String BLOOM_FILTER_NAME_PREFIX = "cache:bloom:";

    @Value("${cache.bloom.name}")
    private String name;

    @Value("${cache.bloom.expectedInsertions:100000}")
    private Long expectedInsertions;

    @Value("${cache.bloom.falseProbability:0.01}")
    private Double falseProbability;

    @Autowired
    private RedissonClient redissonClient;

    private RBloomFilter<T> bloomFilter;

    /**
     * 初始化（项目启动时执行一次）
     */
    @PostConstruct
    public void init() {
        String fullName = BLOOM_FILTER_NAME_PREFIX + name;
        bloomFilter = redissonClient.getBloomFilter(fullName);
        boolean initialized = bloomFilter.tryInit(expectedInsertions, falseProbability);
        if (initialized) {
            log.info("布隆过滤器初始化成功 | name: {}, 预期容量: {}, 误判率: {}",
                    fullName, expectedInsertions, falseProbability);
        } else {
            log.warn("布隆过滤器已存在，跳过初始化 | name: {}", fullName);
        }
    }

    /**
     * 添加元素
     */
    public void add(T value) {
        if (value == null) {
            return;
        }
        bloomFilter.add(value);
    }

    /**
     * 批量添加
     */
    public void addAll(List<T> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        for (T value : values) {
            bloomFilter.add(value);
        }
        log.debug("批量添加布隆过滤器，数量: {}", values.size());
    }

    /**
     * 判断元素是否可能存在
     * @return true=可能存在（放行），false=一定不存在（拦截）
     */
    public boolean mightContain(T value) {
        if (value == null) {
            return false;
        }
        return bloomFilter.contains(value);
    }
}
