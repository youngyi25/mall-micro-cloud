package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.product.domain.entity.SkuInfo;
import com.example.mall.product.domain.entity.SpuInfo;
import com.example.mall.product.service.*;
import com.example.mall.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.mall.api.category.dto.CategoryDTO;
import com.example.mall.api.product.dto.ProductDTO;
import com.example.mall.api.product.dto.ProductDetailDTO;
import com.example.mall.api.product.dto.ProductQueryDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ISkuInfoService skuInfoService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ISpuInfoService spuInfoService;
    @Autowired
    private ISaleAttributeValueService saleAttributeValueService;
    @Override
    public IPage page(ProductQueryDTO queryDTO) {
        // 1. 创建条件构造器
        LambdaQueryChainWrapper<SkuInfo> query = new LambdaQueryChainWrapper<>(skuInfoService.getBaseMapper());
        // 2. 拼接查询条件（用Lambda封闭各个子方法）
        // 2.1 商品名称（模糊）
        buildPriceCondition(query,queryDTO);
        // 2.2 品牌
        buildBrandCondition(query,queryDTO);
        /*
         * 2.3 三级分类
         * 如果有三级分类，则只查三级分类下的商品，否则
         * 如果有二级分类，则查二级分类下的所有商品，否则
         * 查一级分类下的所有商品
         *
         */
        if (queryDTO.getThirdCategoryId() != null){ // 2.3.1 三级分类
            buildThirdCatCondition(query,queryDTO);
        }else if(queryDTO.getSecondCategoryId() != null){ // 2.3.2 二级分类
            buildSecondCatCondition(query,queryDTO);
        }else {  // 2.3.3 一级分类
            buildFirstCatCondition(query,queryDTO);
        }
        // 2.4 价格区间
        buildPriceCondition(query,queryDTO);
        // 2.5 状态（商品状态：0正常，1下架）
        buildStatusCondition(query);
        // 2.6 筛选字段
        query.select(SkuInfo::getId, SkuInfo::getSpuId, SkuInfo::getSkuName, SkuInfo::getPrice,SkuInfo::getSkuDefaultImg,SkuInfo::getHtmlUrl);
        // 3. 分页查询
        Page page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page pageResult = query.page(page);
        // 4. 转换为DTO
        List<ProductDTO> listDTO = JsonUtils.toList(pageResult.getRecords(), ProductDTO.class);
        pageResult.setRecords(listDTO);

        return pageResult;
    }

    /** 商品名称模糊 */
    private void buildNameCondition(LambdaQueryChainWrapper<SkuInfo> wrapper,ProductQueryDTO dto) {
        String name = dto.getProductName();
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(SkuInfo::getSkuName, name.trim());
        }
    }

    /** 品牌精准 */
    private void buildBrandCondition(LambdaQueryChainWrapper<SkuInfo> wrapper,ProductQueryDTO dto) {
        if (dto.getBrandId() != null) {
            wrapper.eq(SkuInfo::getBrandId, dto.getBrandId());
        }
    }

    /** 三级分类 */
    private void buildThirdCatCondition(LambdaQueryChainWrapper<SkuInfo> wrapper,ProductQueryDTO dto) {
        if (dto.getThirdCategoryId() != null) {
            wrapper.eq(SkuInfo::getCategoryId, dto.getThirdCategoryId());
        }
    }

    /** 二级分类 → 查下级所有三级ID in */
    private void buildSecondCatCondition(LambdaQueryChainWrapper<SkuInfo> wrapper,ProductQueryDTO dto) {
        Integer secondId = dto.getSecondCategoryId();
        if (secondId == null) return;
        List<Integer> thirdIds = getThirdIdsBySecondId(secondId); // 获取三级分类ID
        if (!thirdIds.isEmpty()) {
            wrapper.in(SkuInfo::getCategoryId, thirdIds);
        }
    }

    /** 一级分类 → 查全下级二、三级ID in */
    private void buildFirstCatCondition(LambdaQueryChainWrapper<SkuInfo> wrapper,ProductQueryDTO dto) {
        Integer firstId = dto.getFirstCategoryId();
        if (firstId == null) return;
        List<Integer> thirdIds = getThirdIdsByFirstId(firstId); // 获取三级分类ID
        if (!thirdIds.isEmpty()) {
            wrapper.in(SkuInfo::getCategoryId, thirdIds);
        }
    }


    /** 价格区间 */
    private void buildPriceCondition(LambdaQueryChainWrapper<SkuInfo> wrapper,ProductQueryDTO dto) {
        if (dto.getMinPrice() != null && dto.getMaxPrice() != null) {
            wrapper.between(SkuInfo::getPrice, dto.getMinPrice(), dto.getMaxPrice());
        }
    }

    /** 固定状态：正常上架 */
    private void buildStatusCondition(LambdaQueryChainWrapper<SkuInfo> wrapper) {
        wrapper.eq(SkuInfo::getStatus, 0);
    }

    /** 根据二级分类ID → 所有三级ID */
    private List<Integer> getThirdIdsBySecondId(Integer secondCatId) {
        return categoryService.listChildren(secondCatId).stream()
                .map(CategoryDTO::getId)
                .collect(Collectors.toList());
    }

    /** 根据一级分类ID → 递归查出所有三级ID */
    private List<Integer> getThirdIdsByFirstId(Integer firstCatId) {
        // 一级下所有二级
        List<Integer> secondIds = categoryService.listChildren(firstCatId).stream()
                .map(CategoryDTO::getId)
                .collect(Collectors.toList());
        // 所有二级汇总下级三级
        return secondIds.stream()
                .flatMap(secId -> categoryService.listChildren(secId).stream().map(CategoryDTO::getId))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDetailDTO getBySpuId(Long spuId) {
        // 1. 获取SPU信息
        SpuInfo spuInfo = spuInfoService.getById(spuId);
        // 2. 获取SPU销售属性
        Map<String, List<String>> attrMap = saleAttributeValueService.listBySpuId(spuId);
        // 3. 获取SPU商品列表
        List<ProductDTO> productDTOs = skuInfoService.listBySpuId(spuId);
        // 4. 封装并返回
        return new ProductDetailDTO(spuInfo.getSpuName(), attrMap, productDTOs);
    }


}
