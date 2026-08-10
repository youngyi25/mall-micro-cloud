package com.example.mall.config;

import java.time.Duration;
import java.util.Objects;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 配置Redis键的序列化方式
     * @return StringRedisSerializer，用于序列化和反序列化Redis中的键
     */
    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 配置Redis值的序列化方式
     * @return GenericJackson2JsonRedisSerializer，使用Jackson库以JSON格式序列化和反序列化Redis中的值
     */
    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    /**
     * 配置缓存管理器，使用Redis作为缓存后端
     *
     * @param redisConnectionFactory Redis连接工厂，用于创建Redis连接
     * @return RedisCacheManager，Redis缓存管理器实例
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 配置序列化，解决乱码的问题，设置缓存条目的默认过期时间
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置键的序列化器
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                // 设置值的序列化器
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                // 设置缓存条目的默认过期时间为12小时
                .entryTtl(Duration.ofHours(12));
        // 创建非锁定的Redis缓存写入器
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(Objects.requireNonNull(redisConnectionFactory));
        // 返回Redis缓存管理器实例，使用上述配置
        return new RedisCacheManager(redisCacheWriter, config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key 和 HashKey 使用 String 序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 和 HashValue 使用 JSON 序列化
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;
    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 单机模式
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setPassword(password)
                .setConnectionPoolSize(10) // 连接池大小
                .setConnectionMinimumIdleSize(5) // 最小空闲连接数
                .setTimeout(3000) // 连接超时时间
                .setRetryAttempts(3) // 重试次数
                .setRetryInterval(1000); // 重试间隔时间
        // 设置看门狗超时时间（默认30秒）
        config.setLockWatchdogTimeout(30000);
        return Redisson.create(config);
    }
}
