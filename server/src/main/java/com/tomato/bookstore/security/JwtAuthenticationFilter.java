package com.tomato.bookstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** JWT 认证过滤器 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  // 无需验证的路径列表
  private final List<String> PUBLIC_PATHS = Arrays.asList("/user/register", "/user/login", "/");

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

    // 检查请求路径是否在公开路径列表中，如果是则直接放行
    String path = request.getServletPath();
    if (isPublicPath(path)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 从请求头中提取 JWT，如果不存在则放行
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 从 JWT 中提取用户名和用户 ID，如果验证通过则设置用户认证信息
    try {
      final String jwt = authHeader.substring(7);
      final String username = jwtTokenProvider.extractUsername(jwt);
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (jwtTokenProvider.validateToken(jwt, userDetails)) {
          Long userId = jwtTokenProvider.extractUserId(jwt);
          UserPrincipal principal =
              new UserPrincipal(
                  userDetails.getUsername(),
                  userDetails.getPassword(),
                  userDetails.getAuthorities(),
                  userId);
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  principal, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    } catch (Exception e) {
      logger.error("Error occurs when processing JWT token: " + e.getMessage());
    } finally {
      filterChain.doFilter(request, response);
    }
  }

  /**
   * 检查请求路径是否在公开路径列表中
   *
   * @param currentPath 当前请求路径
   * @return 是否在公开路径列表中
   */
  private boolean isPublicPath(String currentPath) {
    return PUBLIC_PATHS.stream().anyMatch(publicPath -> currentPath.equals(publicPath));
  }
}
