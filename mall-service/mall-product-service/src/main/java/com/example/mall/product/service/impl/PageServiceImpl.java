package com.example.mall.product.service.impl;

import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.product.service.IPageService;
import com.example.mall.product.service.IProductService;
import com.example.mall.product.service.ISkuInfoService;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import com.example.mall.api.product.dto.ProductDetailDTO;
import com.example.mall.api.oss.OssFeignClient;

import org.thymeleaf.context.Context;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PageServiceImpl implements IPageService {
    @Autowired
    private IProductService productService;
    @Autowired
    private ISkuInfoService skuInfoService;
    @Autowired
    private OssFeignClient ossFeignClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Override
    public String generateProductTemplate(Long spuId) throws Exception {
        Map<String,Object> model = new HashMap<>();
        // 1 创建Thymeleaf模版的上下文对象
        Context context = new Context();
        // 2 设置模板数据
        // 2.1 获取商品详情数据
        ProductDetailDTO data = productService.getBySpuId(spuId);
        // 2.2 添加数据
        model.put("productDetail",data);
        context.setVariables(model);
        // 3 静态页面生成
        // 3.1 模板渲染至内存字符串，不落地本地文件
        String htmlContent = templateEngine.process("item-template", context);
        String fileName = spuId + ".html";
        // 3.2 创建字节数组
        byte[] htmlBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
        // 4. Feign上传OSS
        Result<String> result = ossFeignClient.uploadByte(htmlBytes, fileName, "text/html", "product/html");
        if (!result.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
            throw new BusinessException(result.getCode(), result.getMsg());
        }
        String ossUrl = (String)result.getData();
        // 5. 将静态页面地址保存至数据表
        skuInfoService.updateBySpuId(spuId,ossUrl);
        return ossUrl;
    }
}
