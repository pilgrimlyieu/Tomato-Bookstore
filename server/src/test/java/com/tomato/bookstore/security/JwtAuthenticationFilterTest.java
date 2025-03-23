package com.tomato.bookstore.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tomato.bookstore.controller.AuthController;
import com.tomato.bookstore.controller.HomeController;
import com.tomato.bookstore.controller.UserController;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.dto.UserDTO;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({UserController.class, AuthController.class, HomeController.class})
@Import(SecurityTestConfig.class)
public class JwtAuthenticationFilterTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private JwtTokenProvider jwtTokenProvider;

  @MockitoBean private UserDetailsService userDetailsService;

  @MockitoBean private UserController userController;

  @MockitoBean private AuthController authController;

  @BeforeEach
  void setup() {
    when(userController.getProfile(any())).thenReturn(ApiResponse.success(new UserDTO()));
    when(authController.login(any())).thenReturn(ApiResponse.success("token"));
  }

  @Test
  @DisplayName("有效 JWT 令牌的请求")
  void requestWithValidToken() throws Exception {
    // 准备
    String validToken = "valid.jwt.token";
    User userDetails = new User("testuser", "password", new ArrayList<>());

    when(jwtTokenProvider.extractUsername(validToken)).thenReturn("testuser");
    when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
    when(jwtTokenProvider.validateToken(validToken, userDetails)).thenReturn(true);
    when(jwtTokenProvider.extractUserId(validToken)).thenReturn(1L);

    // 执行与验证
    mockMvc
        .perform(get("/user/profile").header("Authorization", "Bearer " + validToken))
        .andExpect(status().isOk());

    verify(jwtTokenProvider).extractUsername(validToken);
    verify(userDetailsService).loadUserByUsername("testuser");
    verify(jwtTokenProvider).validateToken(validToken, userDetails);
    verify(jwtTokenProvider).extractUserId(validToken);
  }

  @Test
  @DisplayName("无效 JWT 令牌的请求")
  void requestWithInvalidToken() throws Exception {
    // 准备
    String invalidToken = "invalid.jwt.token";
    User userDetails = new User("testuser", "password", new ArrayList<>());

    when(jwtTokenProvider.extractUsername(invalidToken)).thenReturn("testuser");
    when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
    when(jwtTokenProvider.validateToken(invalidToken, userDetails)).thenReturn(false);

    // 执行与验证
    mockMvc
        .perform(get("/user/profile").header("Authorization", "Bearer " + invalidToken))
        .andExpect(status().isForbidden());

    verify(jwtTokenProvider).extractUsername(invalidToken);
    verify(userDetailsService).loadUserByUsername("testuser");
    verify(jwtTokenProvider).validateToken(invalidToken, userDetails);
    verify(jwtTokenProvider, never()).extractUserId(anyString());
  }

  @Test
  @DisplayName("没有 JWT 令牌的请求")
  void requestWithoutToken() throws Exception {
    // 执行与验证
    mockMvc.perform(get("/user/profile")).andExpect(status().isForbidden());

    verify(jwtTokenProvider, never()).extractUsername(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }

  @Test
  @DisplayName("公共路径无需认证")
  void publicPathNoAuthentication() throws Exception {
    // 执行与验证
    mockMvc.perform(get("/")).andExpect(status().isOk());

    verify(jwtTokenProvider, never()).extractUsername(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }
}
