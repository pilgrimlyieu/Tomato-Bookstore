package com.tomato.bookstore.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/** 支付宝配置类 */
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "alipay")
@Getter
public class AlipayConfig {
  /** 应用 ID */
  private String appId;

  /** 应用私钥 */
  private String appPrivateKey;

  /** 支付宝公钥 */
  private String alipayPublicKey;

  /** 异步通知地址 */
  private String notifyUrl;

  /** 同步跳转地址 */
  private String returnUrl;

  /** 支付宝网关 */
  private String serverUrl;

  /** 签名类型 */
  private String signType = "RSA2";

  /** 格式 */
  private String format = "json";

  /** 编码 */
  private String charset = "UTF-8";

  @Bean
  public AlipayClient alipayClient() {
    return new DefaultAlipayClient(
        serverUrl, appId, appPrivateKey, format, charset, alipayPublicKey, signType);
  }
}
