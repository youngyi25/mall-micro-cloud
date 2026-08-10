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

    @Override
    public List<SeckillActivityDTO> listActivity() {
        List<SeckillActivity> list = lambdaQuery()
                .gt(SeckillActivity::getEndTime, LocalDateTime.now())
                .orderByAsc(SeckillActivity::getStartTime)
                .last("limit 5")
                .list();
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
