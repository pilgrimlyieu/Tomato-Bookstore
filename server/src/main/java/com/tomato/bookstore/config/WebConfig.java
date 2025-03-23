package com.tomato.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置
 *
 * <p>该类配置了 Web 相关设置，包括跨域请求处理、资源处理等。
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
  /**
   * 配置跨域请求
   *
   * <p>该方法配置了跨域请求的相关设置，包括允许的请求来源、请求方法、请求头等。
   *
   * @param registry CorsRegistry 对象
   */
  @Override
  public void addCorsMappings(@NonNull CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
  }
}
