package com.example.mall.seckill.service.impl;

import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.filter.CacheBloomFilter;
import com.example.mall.seckill.domain.dto.SeckillActivityDTO;
import com.example.mall.seckill.domain.dto.SeckillGoodsDTO;
import com.example.mall.seckill.domain.dto.StockDeductDTO;
import com.example.mall.seckill.service.IPageService;
import com.example.mall.seckill.service.IProductService;
import com.example.mall.seckill.service.ISeckillActivityService;
import com.example.mall.seckill.service.ISeckillGoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService {
    private final ISeckillActivityService seckillActivityService;
    private final ISeckillGoodsService seckillGoodsService;
    private final IPageService pageService;
    private final RedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final CacheBloomFilter<String> cacheBloomFilter;
/**
 * 获取今日秒杀商品列表
 * 该方法首先获取今日所有的秒杀活动，然后根据活动ID查询对应的商品信息
 *
 * @return 返回今日所有秒杀活动的商品列表
 */
    @Override
    public List<SeckillGoodsDTO> listTodaySeckillGoods() {
    // 获取今日所有秒杀活动列表
        List<SeckillActivityDTO> activityList = seckillActivityService.listAllByToday();
    // 从活动列表中提取所有活动ID
        List<Integer> activityIdList = activityList.stream().map(SeckillActivityDTO::getId).toList();
    // 创建商品列表，用于存储所有秒杀商品
        List<SeckillGoodsDTO> goodsList = new ArrayList<>();
    // 遍历所有活动ID，获取每个活动下的商品并添加到商品列表中
        for (Integer activityId : activityIdList) {
            goodsList.addAll(seckillGoodsService.listByActivityId(activityId));
        }
    // 返回包含所有秒杀商品的列表
        return goodsList;
    }

/**
 * 重写生成HTML的方法
 * 获取今日所有秒杀活动，并为每个活动生成产品模板页面
 * @throws Exception 可能抛出的异常
 */
    @Override
    public void generateHtml() throws Exception {
        // 获取今日所有秒杀活动列表
        List<SeckillActivityDTO> activityList = seckillActivityService.listAllByToday();
        // 从活动列表中提取所有活动ID
        List<Integer> activityIdList = activityList.stream().map(SeckillActivityDTO::getId).toList();
        // 遍历每个活动ID，生成对应的产品模板页面
        for (Integer activityId : activityIdList) {
            pageService.generateProductTemplate(activityId);
        }
    }

    @Override
    /**
     * 加载秒杀活动商品库存到Redis缓存中
     * 该方法会获取当天所有的秒杀活动，并将每个活动下的商品库存信息存入Redis
     * 每个商品的库存以"seckill-活动ID-商品SKU ID"为键，库存数量为值存储
     * 设置缓存过期时间为1天
     */
    public void loadStockCache() {
        // 获取当天所有的秒杀活动列表
        List<SeckillActivityDTO> activityList = seckillActivityService.listAllByToday();
        // 从活动列表中提取所有活动ID
        List<Integer> activityIdList = activityList.stream().map(SeckillActivityDTO::getId).toList();
        // 遍历每个活动ID
        for (Integer activityId : activityIdList) {
            // 根据活动ID获取该活动下的所有商品列表
            List<SeckillGoodsDTO> goodsList = seckillGoodsService.listByActivityId(activityId);
            // 遍历每个商品
            for (SeckillGoodsDTO goods : goodsList) {
                // 构建Redis键，格式为"seckill-活动ID-商品SKU ID"
                String key = String.format("seckill-%s-%s", activityId, goods.getSkuId());
                // 将商品库存数量存入Redis
                redisTemplate.opsForValue().set(key, goods.getStoreCount());
                // 设置Redis键的过期时间为1天
                redisTemplate.expire(key, 1, TimeUnit.DAYS);
                cacheBloomFilter.add(key);
            }
        }
    }

/**
 * 扣减库存的方法
 * @param stockDeductDTO 库存扣减数据传输对象，包含活动ID和商品SKU ID等信息
 */
    @Override
    public void decreaseStock(StockDeductDTO stockDeductDTO) {
    // 构造分布式锁的键，格式为：lock:seckill:活动ID:商品SKU ID
        String lockKey = "lock:seckill:" + stockDeductDTO.getActivityId() + ":" + stockDeductDTO.getSkuId();
    // 获取Redisson分布式锁对象
        RLock lock = redissonClient.getLock(lockKey);
    // 构造库存键，格式为：seckill-活动ID-商品SKU ID
        String stockKey = "seckill-" + stockDeductDTO.getActivityId() + "-" + stockDeductDTO.getSkuId();
        if(!cacheBloomFilter.mightContain(stockKey)){
            log.warn("商品不存在");
            throw new BusinessException(7003,"库存不存在");
        }
        try {
        // 尝试获取分布式锁，等待3秒，锁持有时间10秒
            boolean isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);
        // 如果获取锁失败
            if (!isLocked) {
                log.warn("获取分布式锁失败");
            // 抛出业务异常，提示获取分布式锁失败
                throw new BusinessException(7001,"获取分布式锁失败");
            }
            log.info("获取分布式锁成功");
        // 从Redis中获取当前库存数量
            int count = Integer.parseInt(redisTemplate.opsForValue().get(stockKey).toString());
        // 如果库存大于0
            if(count>0){
            // 扣减库存并获取剩余库存数量
                Long remainCount = redisTemplate.opsForValue().decrement(stockKey);
                log.info("商品库存扣减成功，剩余库存：{}", remainCount);

            }else{
            // 如果库存不足
                log.warn("商品库存不足");
            // 抛出业务异常，提示库存不足
                throw new BusinessException(ResultCodeEnum.STOCK_SHORTAGE);
            }
        } catch (InterruptedException e) {
        // 恢复中断状态
            Thread.currentThread().interrupt();
            log.error("获取分布式锁失败", e);
        // 抛出系统异常
            throw new BusinessException(7002,"系统异常");
        } finally {
        // 确保当前线程持有锁的情况下释放锁
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
                log.info("释放分布式锁成功");
            }
        }
    }
}
