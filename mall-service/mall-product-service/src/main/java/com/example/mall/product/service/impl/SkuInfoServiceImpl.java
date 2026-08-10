package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.product.domain.entity.SkuInfo;
import com.example.mall.product.mapper.SkuInfoMapper;
import com.example.mall.product.service.ISkuInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.util.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.mall.api.product.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements ISkuInfoService {
    public List<ProductDTO> listBySpuId(Long spuId) {
        // 创建条件构造器
        LambdaQueryChainWrapper<SkuInfo> query = new LambdaQueryChainWrapper<>(getBaseMapper());
        List<SkuInfo> list = query.eq(SkuInfo::getSpuId, spuId)
                .select(SkuInfo::getId, SkuInfo::getSpuId, SkuInfo::getSkuName, SkuInfo::getPrice, SkuInfo::getSkuDefaultImg, SkuInfo::getImages , SkuInfo::getSkuAttribute)
                .list();
        // 转换为DTO
        return JsonUtils.toList(list, ProductDTO.class);
    }

    @Override
    public void updateBySpuId(Long spuId, String ossUrl) {
        LambdaUpdateChainWrapper<SkuInfo> updateChainWrapper = new LambdaUpdateChainWrapper(baseMapper);
        updateChainWrapper.eq(SkuInfo::getSpuId, spuId)
                .set(SkuInfo::getHtmlUrl, ossUrl)
                .update();
    }

    @Override
    @Transactional
    public void decreaseStock(Map<Long, Integer> deductMap) {

            // 安全检查
            if(deductMap == null || deductMap.isEmpty()){
                // 建议5002错误用枚举表示，方便后续扩展
                throw new BusinessException(5002, "没有有效的商品扣减数据");
            }
            // 提取key(商品ID)
            List<Long> skuIds = deductMap.keySet().stream().collect(Collectors.toList());
            // 查询商品库存信息
            List<SkuInfo> skuInfos = baseMapper.selectByIds(skuIds);
            // 提取更新数据
            List<SkuInfo> updateList = new ArrayList<>();
            for (SkuInfo skuInfo : skuInfos) {
                // 判断库存是否充足
                if (skuInfo.getNum() < deductMap.get(skuInfo.getId())) {
                    throw new BusinessException(ResultCodeEnum.STOCK_SHORTAGE);
                }
                // 扣减库存
                skuInfo.setNum(skuInfo.getNum() - deductMap.get(skuInfo.getId()));
                // 添加更新数据
                updateList.add(skuInfo);
            }
            // 批量更新
            baseMapper.updateById(updateList);

    }

}
