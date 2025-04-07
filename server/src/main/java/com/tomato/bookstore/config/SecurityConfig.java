package com.tomato.bookstore.config;

import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.security.JwtAuthenticationEntryPoint;
import com.tomato.bookstore.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 *
 * <p>该类配置了 Spring Security 的相关设置，包括密码编码器、安全过滤器链等。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final UserDetailsService userDetailsService;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Value("${app.security.public-paths}")
  private String[] publicPaths;

  /**
   * 创建密码编码器
   *
   * <p>该方法创建了一个 BCryptPasswordEncoder 对象，用于对密码进行编码。
   *
   * @return BCryptPasswordEncoder 密码编码器
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 创建安全过滤器链
   *
   * <p>该方法创建了一个安全过滤器链，配置了 Spring Security 的相关设置，包括关闭 CSRF 保护、配置请求授权规则、配置会话管理策略、配置用户详细信息服务、添加 JWT
   * 认证过滤器等。
   *
   * @param http HttpSecurity 对象
   * @return SecurityFilterChain 安全过滤器链
   * @throws Exception 当创建安全过滤器链失败时抛出
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configure(http))
        .authorizeHttpRequests(
            authorizeRequests -> {
              authorizeRequests.requestMatchers(publicPaths).permitAll();
              authorizeRequests
                  .requestMatchers(
                      ApiConstants.USER_REGISTER_PATH,
                      ApiConstants.USER_LOGIN_PATH,
                      ApiConstants.HOME)
                  .permitAll();
              authorizeRequests
                  .requestMatchers(HttpMethod.POST, ApiConstants.ORDER_NOTIFY_PATH)
                  .permitAll();
              authorizeRequests
                  .requestMatchers(HttpMethod.GET, ApiConstants.ORDER_RETURN_PATH)
                  .permitAll();
              authorizeRequests.anyRequest().authenticated();
            })
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .userDetailsService(userDetailsService)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  /**
   * 创建认证管理器
   *
   * <p>该方法创建了一个认证管理器，用于处理用户的认证请求。
   *
   * @param config AuthenticationConfiguration 对象
   * @return AuthenticationManager 认证管理器
   * @throws Exception 当创建认证管理器失败时抛出
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
