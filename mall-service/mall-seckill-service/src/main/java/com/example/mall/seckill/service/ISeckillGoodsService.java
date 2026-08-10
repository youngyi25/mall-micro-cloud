package com.example.mall.seckill.service;

import com.example.mall.seckill.domain.dto.SeckillGoodsDTO;
import com.example.mall.seckill.domain.dto.SeckillGoodsUpdateDTO;
import com.example.mall.seckill.domain.po.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yy
 * @since 2026-08-09
 */
public interface ISeckillGoodsService extends IService<SeckillGoods> {

    /**
     * 按活动id查询
     */
    List<SeckillGoodsDTO> listByActivityId(Integer activityId);

    /**
     * 按活动id删除
     */
    Boolean removeByActivityId(Integer activityId);

    /**
     * 按活动id修改
     */
    Boolean updateByActivityId(SeckillGoodsUpdateDTO seckillGoods);

}
