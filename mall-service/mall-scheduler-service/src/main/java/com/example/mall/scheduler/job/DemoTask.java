package com.example.mall.scheduler.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DemoTask implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("执行静态定时任务时间: " + LocalDateTime.now());
    }
}
