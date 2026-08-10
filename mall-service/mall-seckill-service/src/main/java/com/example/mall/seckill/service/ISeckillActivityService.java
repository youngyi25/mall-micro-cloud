package com.example.mall.seckill.service;

import com.example.mall.seckill.domain.dto.SeckillActivityDTO;
import com.example.mall.seckill.domain.po.SeckillActivity;
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
public interface ISeckillActivityService extends IService<SeckillActivity> {
    /**
     * 获取秒杀活动列表
     * @return
     */
    public List<SeckillActivityDTO> listActivity();
    /**
     * 获取当天的所有活动
     * @return
     */
    List<SeckillActivityDTO> listAllByToday();

}
