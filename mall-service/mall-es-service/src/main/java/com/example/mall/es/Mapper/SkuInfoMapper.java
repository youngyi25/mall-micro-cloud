package com.example.mall.es.Mapper;

import com.example.mall.es.domain.entity.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SkuInfoMapper  extends ElasticsearchRepository<SkuInfo, String> {
    /**
     * 根据分类id查询
     * 方法名：按by字段名匹配
     * @param categoryId
     * @return
     */
    List<SkuInfo> findByCategoryId(Long categoryId);

    /**
     *  根据价格区间查询
     * @param min
     * @param max
     * @return
     */
    List<SkuInfo> findByPriceBetween(Double min, Double max);

}
