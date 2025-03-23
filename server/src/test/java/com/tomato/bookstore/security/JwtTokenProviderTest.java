package com.tomato.bookstore.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.ExpiredJwtException;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {
  @Spy @InjectMocks private JwtTokenProvider jwtTokenProvider;

  private UserDetails userDetails;
  private final String secretKey =
      "dG9tYXRvYm9va3N0b3Jlc2VjcmV0a2V5dG9tYXRvYm9va3N0b3Jlc2VjcmV0a2V5";
  private final long expirationTime = 3600000; // 1 小时

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secretKey);
    ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", expirationTime);

    userDetails = new User("testuser", "password", Collections.emptyList());
  }

  @Test
  @DisplayName("生成 JWT 令牌")
  void generateTokenTest() {
    // 执行
    String token = jwtTokenProvider.generateToken("testuser", 1L);

    // 验证
    assertNotNull(token);
    assertTrue(token.length() > 0);
  }

  @Test
  @DisplayName("从令牌中提取用户名")
  void extractUsernameTest() {
    // 准备
    String token = jwtTokenProvider.generateToken("testuser", 1L);

    // 执行
    String username = jwtTokenProvider.extractUsername(token);

    // 验证
    assertEquals("testuser", username);
  }

  @Test
  @DisplayName("从令牌中提取用户 ID")
  void extractUserIdTest() {
    // 准备
    String token = jwtTokenProvider.generateToken("testuser", 1L);

    // 执行
    Long userId = jwtTokenProvider.extractUserId(token);

    // 验证
    assertEquals(1L, userId);
  }

  @Test
  @DisplayName("验证有效的令牌")
  void validateValidTokenTest() {
    // 准备
    String token = jwtTokenProvider.generateToken("testuser", 1L);

    // 执行
    boolean isValid = jwtTokenProvider.validateToken(token, userDetails);

    // 验证
    assertTrue(isValid);
  }

  @Test
  @DisplayName("验证过期的令牌")
  void validateExpiredTokenTest() {
    // 准备
    ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", -10000); // 设置为负值使令牌立即过期
    String expiredToken = jwtTokenProvider.generateToken("testuser", 1L);
    ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", expirationTime); // 恢复原值

    // 验证
    assertThrows(
        ExpiredJwtException.class,
        () -> {
          jwtTokenProvider.validateToken(expiredToken, userDetails);
        });
  }

  @Test
  @DisplayName("验证用户名不匹配的令牌")
  void validateTokenWithWrongUsernameTest() {
    // 准备
    String token = jwtTokenProvider.generateToken("wronguser", 1L);

    // 执行
    boolean isValid = jwtTokenProvider.validateToken(token, userDetails);

    // 验证
    assertFalse(isValid);
  }

  @Test
  @DisplayName("从令牌中提取过期时间")
  void extractExpirationTest() {
    // 准备
    String token = jwtTokenProvider.generateToken("testuser", 1L);

    // 执行
    Date expiration = jwtTokenProvider.extractExpiration(token);

    // 验证
    assertNotNull(expiration);
    assertTrue(expiration.after(new Date()));
  }
}
