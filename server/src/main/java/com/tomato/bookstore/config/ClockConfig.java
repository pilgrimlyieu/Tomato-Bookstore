package com.tomato.bookstore.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 时钟配置类
 *
 * <p>
 * 提供系统时钟实例，用于日期和时间操作
 */
@Configuration
public class ClockConfig {
  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
