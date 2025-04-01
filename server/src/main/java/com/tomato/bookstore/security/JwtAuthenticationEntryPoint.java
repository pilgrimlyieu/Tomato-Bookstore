package com.tomato.bookstore.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * JWT 认证入口点
 *
 * <p>处理未认证请求的响应
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ApiResponse<Void> apiResponse =
        ApiResponse.error(BusinessErrorCode.USER_UNAUTHORIZED.getCode(), "JWT 令牌无效或已过期", null);

    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
  }
}
