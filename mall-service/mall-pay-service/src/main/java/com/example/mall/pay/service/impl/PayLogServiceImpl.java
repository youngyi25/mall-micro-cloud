package com.example.mall.pay.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.example.mall.pay.domain.po.PayLog;
import com.example.mall.pay.mapper.PayLogMapper;
import com.example.mall.pay.service.IPayLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付记录表 服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-08-04
 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements IPayLogService {

    @Override
    public PayLog getByOrderId(String orderId) {
        LambdaQueryChainWrapper<PayLog> query = new LambdaQueryChainWrapper<>(baseMapper);
        PayLog payLog = query.eq(PayLog::getOrderId, orderId).one();
        return payLog;
    }
}
