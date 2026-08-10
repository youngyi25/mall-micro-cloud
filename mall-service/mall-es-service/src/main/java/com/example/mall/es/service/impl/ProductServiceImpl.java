package com.example.mall.es.service.impl;

import co.elastic.clients.json.JsonData;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.api.product.dto.ProductDTO;
import com.example.mall.api.product.dto.ProductQueryDTO;
import com.example.mall.es.Mapper.SkuInfoMapper;
import com.example.mall.es.domain.entity.SkuInfo;
import com.example.mall.es.service.IProductService;
import com.example.mall.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SkuInfoMapper skuInfoMapper;
    @Override
    public IPage page(ProductQueryDTO queryDTO) {
        ArrayList<Query> querylist = new ArrayList<>();
        if(queryDTO.getProductName()!=null){
            querylist.add(buildMatchQuery("sku_name",queryDTO.getProductName()));
        }
        if(queryDTO.getThirdCategoryId()!=null){
            querylist.add(buildTermQuery("category_id",queryDTO.getThirdCategoryId()));
        }
        if(queryDTO.getBrandId()!=null){
            querylist.add(buildTermQuery("brand_id",queryDTO.getBrandId()));
        }
        if(queryDTO.getMinPrice()!=null&& queryDTO.getMaxPrice()!=null){
            querylist.add(buildRangeQuery("price",queryDTO.getMinPrice(),queryDTO.getMaxPrice()));
        }
        int pagenum = queryDTO.getPageNum().intValue() - 1;
        int pageSize = queryDTO.getPageSize().intValue();
        Pageable pageable = PageRequest.of(pagenum, pageSize);
        // 构建高亮查询
        HighlightQuery highlightQuery = new HighlightQuery(buildHighlightQuery("sku_name"), SkuInfo.class);
        NativeQueryBuilder queryBuilder = NativeQuery.builder();
        queryBuilder
                .withQuery(b->b.bool(m->m.must(querylist)))
                .withPageable(pageable)
                .withHighlightQuery(highlightQuery)
                .withSourceFilter(new FetchSourceFilter(
                        new String[]{
                                "id","spuId","skuName","num","price","skuDefaultImg","images","skuAttribute","htmlUrl"
                        },null
                ));
        SearchHits<SkuInfo> searchHits = elasticsearchTemplate.search(queryBuilder.build(), SkuInfo.class);
        // 转换数据，替换高亮片段
        List<ProductDTO> records = searchHits.stream()
                .map(e -> {
                    Map<String, List<String>> highlights = e.getHighlightFields();
                    String skuName = highlights.get("sku_name") != null ?
                            highlights.get("sku_name").get(0) :
                            e.getContent().getSkuName();
                    ProductDTO obj = JsonUtils.toObj(e.getContent(), ProductDTO.class);
                    obj.setSkuName(skuName);
                    return obj;
                })
                .collect(Collectors.toList());
        Page<ProductDTO> pageResult = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        pageResult.setRecords(records);
        pageResult.setTotal(searchHits.getTotalHits());

        return pageResult;
    }

    /**
     * 构建模糊查询条件
     * @param fieldName
     * @param value
     * @return
     */
    private Query buildMatchQuery(String fieldName, Object value) {
        return Query.of(builder ->
                builder.match(
                        match -> match.field(fieldName).query(value.toString())
                )
        );
    }
    /**
     * 构建精确查询条件
     * @param fieldName
     * @param value
     * @return
     */
    private Query buildTermQuery(String fieldName, Object value) {
        return Query.of(builder ->
                builder.term(term ->
                        term.field(fieldName).value(value.toString())
                )
        );
    }
    /**
     * 构建范围查询条件
     * @param fieldName
     * @param minValue
     * @param maxValue
     * @return
     */
    private Query buildRangeQuery(String fieldName, Object minValue, Object maxValue) {
        return Query.of(builder ->
                builder.range(range ->
                        range.field(fieldName)
                                .gte(JsonData.of(minValue))
                                .lte(JsonData.of(maxValue))
                )
        );
    }

    /**
     * 创建高亮查询条件
     * @param fieldName 高亮字段
     * @return Highlight
     */
    private Highlight buildHighlightQuery(String fieldName) {
        // 配置高亮参数
        HighlightFieldParameters parameters = HighlightFieldParameters.builder()
                .withNumberOfFragments(1)
                .withFragmentSize(100)
                .withPreTags("<strong style=\"color:red;font-size:16px;\">")
                .withPostTags("</strong>")
                .withRequireFieldMatch(true)
                .build();
        // 配置高亮字段
        HighlightField highlightField = new HighlightField(fieldName, parameters);
        return new Highlight(List.of(highlightField));
    }
}
