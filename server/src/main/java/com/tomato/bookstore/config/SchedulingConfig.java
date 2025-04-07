package com.tomato.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** 定时任务配置类 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
  // 配置定时任务，具体的任务在 OrderServiceImpl 中实现
}
