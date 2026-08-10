package com.example.mall.seckill.controller;


import com.example.mall.seckill.domain.dto.SeckillActivityDTO;
import com.example.mall.seckill.service.ISeckillActivityService;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/seckillActivity")
public class SeckillActivityController {
    @Autowired
    private ISeckillActivityService seckillActivityService;
    @GetMapping("/list")
    public Result<List<SeckillActivityDTO>> list() {
        List<SeckillActivityDTO> list = seckillActivityService.listActivity();
        return Result.success(list);
    }

}
