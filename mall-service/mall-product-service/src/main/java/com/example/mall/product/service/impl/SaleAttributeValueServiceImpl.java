package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.example.mall.product.domain.entity.SaleAttributeValue;
import com.example.mall.product.mapper.SaleAttributeValueMapper;
import com.example.mall.product.service.ISaleAttributeValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Service
public class SaleAttributeValueServiceImpl extends ServiceImpl<SaleAttributeValueMapper, SaleAttributeValue> implements ISaleAttributeValueService {

    @Override
    public Map<String, List<String>> listBySpuId(Long spuId) {
        // 创建条件构造器
        LambdaQueryChainWrapper<SaleAttributeValue> query = new LambdaQueryChainWrapper<>(baseMapper);
        // 1. 获取属性名称
        List<SaleAttributeValue> attributeNames = query.eq(SaleAttributeValue::getSpuId, spuId).list();
        // 2. 创建属性名称 -> 属性值列表的映射
        Map<String, List<String>> attrMap = buildAttrMap(attributeNames,spuId);
        return attrMap;
    }

    /** 创建属性名称 -> 属性值列表的映射，比较SPUID和销售属性ID ,封装成Map<属性名，属性值>*/
    private Map<String,List<String>> buildAttrMap(List<SaleAttributeValue> attributeNames,Long spuId) {
        Map<String, List<String>> attrMap = attributeNames.stream()
                .map(attr -> {
                    LambdaQueryChainWrapper<SaleAttributeValue> query = new LambdaQueryChainWrapper<>(baseMapper);
                    List<String> valueList = query.eq(SaleAttributeValue::getSpuId, spuId)
                            .eq(SaleAttributeValue::getBaseSaleAttrId, attr.getBaseSaleAttrId())
                            .select(SaleAttributeValue::getSaleAttrValueName)
                            .list()
                            .stream()
                            .map(SaleAttributeValue::getSaleAttrValueName)
                            .collect(Collectors.toList());
                    return new AbstractMap.SimpleEntry<>(attr.getSaleAttrName(), valueList);
                })
                // 第三个参数：重复key时，新值替换旧值 + 去重
                .collect(Collectors.toConcurrentMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue,
                        (oldList, newList) -> {
                            oldList = newList;
                            return oldList.stream().distinct().toList();
                        }
                ));
        return attrMap;
    }

}
