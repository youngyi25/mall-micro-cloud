package com.example.mall.seckill.service;

import java.util.List;

public interface IPageService {
    /**
     * 生成秒杀商品静态页面
     * @param activityId 活动ID
     * @return 生成的静态页面路径列表
     */
    List<String> generateProductTemplate(Integer activityId) throws Exception;
}
