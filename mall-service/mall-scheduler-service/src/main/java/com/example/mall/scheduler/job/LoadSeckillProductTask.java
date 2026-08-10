package com.example.mall.scheduler.job;

import com.example.mall.api.seckill.feign.SeckillProductFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoadSeckillProductTask implements SimpleJob {
    private final SeckillProductFeignClient seckillProductFeignClient;
    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("开始加载秒杀商品");
        seckillProductFeignClient.listTodaySeckillGoods();
        log.info("加载秒杀商品完成");
        log.info("开始生成静态页面");
        try {
            seckillProductFeignClient.generateHtml();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("生成静态页面完成");
        log.info("开始加载缓存");
        seckillProductFeignClient.loadStockCache();
        log.info("加载缓存完成");
    }
}
