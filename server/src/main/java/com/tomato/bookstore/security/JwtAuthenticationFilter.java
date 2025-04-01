package com.tomato.bookstore.security;

import com.tomato.bookstore.constant.ApiConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 认证过滤器
 *
 * <p>该过滤器负责从请求中提取 JWT，并进行验证、解析，将认证信息存入 SecurityContext
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  /** 不需要认证的公共路径列表 */
  private final List<String> PUBLIC_PATHS =
      Arrays.asList(
          ApiConstants.HOME, ApiConstants.USER_LOGIN_PATH, ApiConstants.USER_REGISTER_PATH);

  /**
   * 过滤请求，检查 JWT 并设置用户认证信息
   *
   * @param request HTTP 请求
   * @param response HTTP 响应
   * @param filterChain 过滤器链
   * @throws ServletException 如果发生 Servlet 异常
   * @throws IOException 如果发生 IO 异常
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    // 对于公共路径，直接通过
    String requestPath = request.getServletPath();
    if (isPublicPath(requestPath)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 获取 JWT
    String jwt = getJwtFromRequest(request);
    if (StringUtils.hasText(jwt)) {
      try {
        String username = jwtTokenProvider.extractUsername(jwt);
        if (StringUtils.hasText(username)
            && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          if (jwtTokenProvider.validateToken(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            Long userId = jwtTokenProvider.extractUserId(jwt);
            if (userDetails instanceof UserPrincipal) {
              log.debug("用户「{}」（ID: {}）认证成功（自定义用户）", username, userId);
            } else {
              log.debug("用户「{}」（ID: {}）认证成功", username, userId);
            }
          }
        }
      } catch (Exception e) {
        log.error("JWT 认证失败：{}", e.getMessage());
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * 从请求中获取 JWT
   *
   * @param request HTTP 请求
   * @return JWT 或 null
   */
  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  /**
   * 判断是否为公共路径
   *
   * @param path 请求路径
   * @return 是否为公共路径
   */
  private boolean isPublicPath(String path) {
    return PUBLIC_PATHS.contains(path);
  }
}
