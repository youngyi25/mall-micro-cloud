package com.example.mall.seckill.service.impl;

import com.example.mall.seckill.domain.dto.SeckillGoodsDTO;
import com.example.mall.seckill.service.IPageService;
import com.example.mall.seckill.service.ISeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PageServiceImpl implements IPageService {
    @Value("${file.path}")
    private String path;
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public List<String> generateProductTemplate(Integer activityId) throws Exception {
        // 1 创建Thymeleaf模版的上下文对象
        Context context = new Context();
        // 2 设置模板数据
        // 2.1 获取活动商品详情数据
        List<SeckillGoodsDTO> list = seckillGoodsService.listByActivityId(activityId);
        // 2.2 添加数据
        // 存储生成的静态页面的URL
        List<String> urls = new ArrayList<>();
        for (SeckillGoodsDTO data : list) {
            context.setVariable("product", data);
            // 3 指定静态页面保存路径和名称（以activityId-skuId命名）
            String fileName = activityId + "-" + data.getSkuId() + ".html";
            File file = new File(path, fileName);
            // 确保目录存在
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            PrintWriter printWriter = new PrintWriter(file, "UTF-8");
            // 4 静态页面生成
            templateEngine.process("seckill-item-template", context, printWriter);
            printWriter.close();
            urls.add(path + "/" + fileName);
        }
        return urls;
    }
}
