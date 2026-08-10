package com.example.mall.pay.config;

import com.alipay.api.AlipayConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayConfiguration {
    // 支付宝的AppId
    private String appId;
    // 支付宝网关地址
    private String gatewayUrl;
    // 应用私钥
    private String privateKey;
    // 支付宝公钥
    private String alipayPublicKey;
    // 支付宝通知本地的完成地址
    private String notifyUrl;
    // 支付宝回调地址
    private String returnUrl;
    // 支付成功返回页面
    private String successUrl;
    /**
     * 该静态方法用于创建并配置一个实例，
     * 设置支付宝接口调用所需的基本参数:
     * 如服务器地址、AppId、密钥、编码格式等，返回配置好的对象以供使用。
     * @return
     */
    public AlipayConfig getAlipayConfig() {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(gatewayUrl);
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }
}
