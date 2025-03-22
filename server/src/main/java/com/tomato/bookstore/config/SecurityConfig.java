package com.tomato.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tomato.bookstore.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security 配置
 *
 * 该类配置了 Spring Security 的相关设置，包括密码编码器、安全过滤器链等。
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * 创建密码编码器
     *
     * 该方法创建了一个 BCryptPasswordEncoder 对象，用于对密码进行编码。
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
     * 该方法创建了一个安全过滤器链，配置了 Spring Security 的相关设置，包括关闭 CSRF
     * 保护、配置请求授权规则、配置会话管理策略、配置用户详细信息服务、添加 JWT 认证过滤器等。
     *
     * @param http HttpSecurity 对象
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 当创建安全过滤器链失败时抛出
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/user/register", "/user/login", "/").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 创建认证管理器
     *
     * 该方法创建了一个认证管理器，用于处理用户的认证请求。
     *
     * @param config AuthenticationConfiguration 对象
     * @return AuthenticationManager 认证管理器
     * @throws Exception 当创建认证管理器失败时抛出
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
