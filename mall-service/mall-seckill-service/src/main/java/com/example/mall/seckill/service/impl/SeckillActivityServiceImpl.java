package com.example.mall.seckill.service.impl;

import com.example.mall.seckill.domain.dto.SeckillActivityDTO;
import com.example.mall.seckill.domain.po.SeckillActivity;
import com.example.mall.seckill.mapper.SeckillActivityMapper;
import com.example.mall.seckill.service.ISeckillActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.util.JsonUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-08-09
 */
@Service
public class SeckillActivityServiceImpl extends ServiceImpl<SeckillActivityMapper, SeckillActivity> implements ISeckillActivityService {

/**
 * 获取当前有效的秒杀活动列表
 * 查询结束时间晚于当前时间的活动，并按开始时间升序排列，限制返回最多5条记录
 *
 * @return 返回SeckillActivityDTO列表，包含当前有效的秒杀活动信息
 */
    @Override
    public List<SeckillActivityDTO> listActivity() {
    // 使用Lambda查询条件构建器查询数据库
    // 1. 查询结束时间大于当前时间的活动
    // 2. 按开始时间升序排序
    // 3. 限制查询结果为5条记录
        List<SeckillActivity> list = lambdaQuery()
                .gt(SeckillActivity::getEndTime, LocalDateTime.now())
                .orderByAsc(SeckillActivity::getStartTime)
                .last("limit 5")
                .list();
    // 将查询结果转换为DTO对象列表并返回
        return JsonUtils.toList(list, SeckillActivityDTO.class);

    }

    @Override
    public List<SeckillActivityDTO> listAllByToday() {
        LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(59).withSecond(59);
        LocalDateTime endTime = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        List<SeckillActivity> list = lambdaQuery()
                .between(SeckillActivity::getStartTime, startTime, endTime)
                .list();
        return JsonUtils.toList(list, SeckillActivityDTO.class);

    }
}
