package com.example.mall.Service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.product.ProductServiceApplication;
import com.example.mall.product.domain.entity.SkuInfo;
import com.example.mall.product.service.ISkuInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = ProductServiceApplication.class)
public class SkuInfoServiceTest {

    @Autowired
    private ISkuInfoService skuInfoService;

    @Test
    public void testGetById() {
        SkuInfo skuInfo = skuInfoService.getById(2);
        System.out.println(skuInfo);
    }

    @Test
    public void testSaveOrUpdate() {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(100001L);
        skuInfo.setSpuId(100001L);
        skuInfo.setPrice(1000.0);
        skuInfo.setSkuName("测试商品");
        
        // 先删除可能存在的记录
        skuInfoService.removeById(100001L);
        
        // 再插入新记录
        skuInfoService.save(skuInfo);
        
        SkuInfo skuInfo1 = new SkuInfo();
        skuInfo1.setId(100001L);
        skuInfo1.setPrice(2000.0);
        skuInfoService.updateById(skuInfo1);
    }

    @Test
    public void testDelete() {
        skuInfoService.removeById(100001L);
    }

    @Test
    public void testSaveOrUpdateBatch() {
        SkuInfo skuInfo1 = new SkuInfo();
        skuInfo1.setSpuId(200001L);
        skuInfo1.setPrice(1000.0);
        skuInfo1.setSkuName("测试商品1");
        SkuInfo skuInfo2 = new SkuInfo();
        skuInfo2.setSpuId(200002L);
        skuInfo2.setPrice(2000.0);
        skuInfo2.setSkuName("测试商品2");
        List<SkuInfo> list = new java.util.ArrayList<>();
        list.add(skuInfo1);
        list.add(skuInfo2);
        skuInfoService.saveOrUpdateBatch(list);
    }

    @Test
    public void testDeleteBatchIds() {
        List<Long> list = new java.util.ArrayList<>();
        list.add(100001L);
        list.add(100002L);
        skuInfoService.removeBatchByIds(list);
    }
    @Test
    // 测试查询类别为61且价格5000-7000的sku信息
    public void testQuery() {
        LambdaQueryChainWrapper<SkuInfo> queryChainWrapper =
                new LambdaQueryChainWrapper<>(skuInfoService.getBaseMapper());
        // 链式调用：多条件为并且。注意：设置非法SQL插件，强制走索引，如果没有创建索引，则报非法SQL，SQL未使用到索引
        List<SkuInfo> list = queryChainWrapper
                .eq(SkuInfo::getCategoryId, 61) // 筛选条件
                .between(SkuInfo::getPrice, 5000, 7000) // 筛选条件
                .select(SkuInfo::getId, SkuInfo::getSkuName, SkuInfo::getPrice) // 筛选字段
                .list();// 最终方法（结果集）
        System.out.println(list);
    }
    @Test
    // 测试条件更新：类别为61且价格<1000的sku库存+100
    public void testUpdate() {
        LambdaUpdateChainWrapper<SkuInfo> updateChainWrapper =
                new LambdaUpdateChainWrapper<>(skuInfoService.getBaseMapper());

        updateChainWrapper
                .eq(SkuInfo::getCategoryId, 61) // 筛选条件
                .lt(SkuInfo::getPrice, 1000) // 筛选条件
                //.set(SkuInfo::getNum,  100) // 更新
                .setSql("num = num + 100")
                .update();
    }
    @Test
    // 测试分页查询
    public void testPage() {
        // 创建条件构造器
        LambdaQueryChainWrapper<SkuInfo> queryChainWrapper =
                new LambdaQueryChainWrapper<>(skuInfoService.getBaseMapper());
        // 创建分页对象
        Page<SkuInfo> page = new Page<>(1, 3);
        Page<SkuInfo> pageResult = queryChainWrapper
                .eq(SkuInfo::getCategoryId, 61)
                .ge(SkuInfo::getPrice, 5000)
                .select(SkuInfo::getId, SkuInfo::getSkuName, SkuInfo::getPrice)
                .page(page);
        System.out.println("总记录数：" + pageResult.getTotal());
        System.out.println("当前页码：" + pageResult.getCurrent());
        System.out.println("页面大小：" + pageResult.getSize());
        System.out.println("总页数：" + pageResult.getPages());
        System.out.println("结果集：" + pageResult.getRecords());
    }
}
