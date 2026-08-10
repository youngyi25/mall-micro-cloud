package com.example.mall.seckill.controller;

import com.example.mall.seckill.domain.dto.SeckillGoodsDTO;
import com.example.mall.seckill.domain.dto.SeckillGoodsUpdateDTO;
import com.example.mall.seckill.service.ISeckillGoodsService;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yy
 * @since 2026-08-09
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @GetMapping("/activity/{id}")
    public Result<List<SeckillGoodsDTO>> listByActivityId(@PathVariable Integer id) {
        List<SeckillGoodsDTO> list = seckillGoodsService.listByActivityId(id);
        return Result.success(list);
    }
    @PutMapping("/activity/update")
    public Result updateByActivityId(@RequestBody SeckillGoodsUpdateDTO seckillGoods) {
        seckillGoodsService.updateByActivityId(seckillGoods);
        return Result.success();
    }
    @DeleteMapping("/activity/{id}")
    public Result deleteByActivityId(@PathVariable Integer id) {
        seckillGoodsService.removeByActivityId(id);
        return Result.success();
    }
}
