package com.example.mall.seckill.service.impl;

import com.example.mall.seckill.domain.dto.SeckillGoodsDTO;
import com.example.mall.seckill.domain.dto.SeckillGoodsUpdateDTO;
import com.example.mall.seckill.domain.po.SeckillGoods;
import com.example.mall.seckill.mapper.SeckillGoodsMapper;
import com.example.mall.seckill.service.ISeckillGoodsService;
import com.example.mall.util.JsonUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {

    @Override
    public List<SeckillGoodsDTO> listByActivityId(Integer activityId) {
        List<SeckillGoods> list = lambdaQuery()
                .eq(SeckillGoods::getActivityId, activityId)
                .list();
        return JsonUtils.toList(list, SeckillGoodsDTO.class);
    }

    @Override
    public Boolean removeByActivityId(Integer activityId) {
        return lambdaUpdate()
                .eq(SeckillGoods::getActivityId, activityId)
                .remove();
    }

    @Override
    public Boolean updateByActivityId(SeckillGoodsUpdateDTO seckillGoods) {
        SeckillGoods obj = JsonUtils.toObj(seckillGoods, SeckillGoods.class);
        return lambdaUpdate()
                .eq(SeckillGoods::getActivityId, seckillGoods.getActivityId())
                .update(obj);
    }

}
