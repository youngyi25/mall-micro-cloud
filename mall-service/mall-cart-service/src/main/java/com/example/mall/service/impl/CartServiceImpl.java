package com.example.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.domain.dto.CartDTO;
import com.example.mall.domain.dto.CartQueryDTO;
import com.example.mall.domain.entity.Cart;
import com.example.mall.service.ICartService;
import com.example.mall.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {
    private final MongoTemplate mongoTemplate;
    @Override
    public void addToCart(Cart cart) {
        cart.setId(cart.getSkuId()+"-"+cart.getUserId());
        cart.setCreateTime(LocalDateTime.now());
        Cart old = mongoTemplate.findById(cart.getId(), Cart.class);
        if (old != null) {
            cart.setQuantity(old.getQuantity()+cart.getQuantity());

        }
        mongoTemplate.save(cart);
    }

    @Override
    public void addToCart(List<Cart> carts) {
        carts.stream().forEach(cart ->{
            cart.setId(cart.getSkuId()+"-"+cart.getUserId());
            cart.setCreateTime(LocalDateTime.now());
            Cart old = mongoTemplate.findById(cart.getId(), Cart.class);
            if (old != null)
                cart.setQuantity(old.getQuantity()+cart.getQuantity());
        });
        mongoTemplate.remove(Query.query(Criteria.where("id").in(carts.stream().map(Cart::getId).toArray())), Cart.class);

        mongoTemplate.insertAll(carts);}

    @Override
    public Page<CartDTO> page(CartQueryDTO cartQueryDTO) {
        // 1. 创建条件构造器
        Criteria criteria = new Criteria();
        // 2. 拼接筛选条件
        if(cartQueryDTO.getUserId() != null){
            criteria.and("user_id").is(cartQueryDTO.getUserId());
        }
        if (cartQueryDTO.getSkuName() != null) {
            criteria.and("sku_name").regex(cartQueryDTO.getSkuName());
        }
        if (cartQueryDTO.getMinPrice() != null) {
            criteria.and("price").gt(cartQueryDTO.getMinPrice());
        }
        if (cartQueryDTO.getMaxPrice() != null) {
            criteria.and("price").lt(cartQueryDTO.getMaxPrice());
        }
        // 3. 查询满足条件的总文档数（必与分页参数分离）
        long total = mongoTemplate.count(Query.query(criteria), Cart.class);
        // 4. 执行查询
        // 4.1 创建查询对象
        Query query = Query.query(criteria)
                .skip((cartQueryDTO.getPageNum() - 1) * cartQueryDTO.getPageSize())
                .limit(cartQueryDTO.getPageSize())
                .with(Sort.by(Sort.Direction.DESC, "create_time"));
        // 4.2 执行查询
        List<Cart> carts =mongoTemplate.find(query, Cart.class);

        // 5. 创建分页对象
        Page<CartDTO> pageResult = new Page<>(cartQueryDTO.getPageNum(), cartQueryDTO.getPageSize(), total);
        List<CartDTO> cartDTOs = JsonUtils.toList(carts, CartDTO.class);
        pageResult.setRecords(cartDTOs);

        return pageResult;
    }

    @Override
    public List<CartDTO> list(String userId) {
        Query query = Query.query(Criteria.where("user_id").is(userId));
        List<Cart> carts = mongoTemplate.find(query, Cart.class);
        List<CartDTO> cartDTOS = JsonUtils.toList(carts, CartDTO.class);
        return cartDTOS;
    }

    @Override
    @Transactional
    public void removeCart(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), Cart.class);

    }

    @Override
    public void removeCart(List<String> ids) {
        Query query = Query.query(Criteria.where("id").in(ids));
        mongoTemplate.remove(query, Cart.class);
    }
}
