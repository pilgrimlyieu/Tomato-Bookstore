package com.tomato.bookstore.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 阿里云 OSS 配置类 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
public class OssConfig {
  private String endpoint;
  private String accessKeyId;
  private String accessKeySecret;
  private String bucketName;

  /**
   * 创建 OSS 客户端 Bean
   *
   * @return OSS 客户端
   */
  @Bean
  public OSS ossClient() {
    return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
  }
}
