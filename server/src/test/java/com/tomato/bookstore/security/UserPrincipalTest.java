package com.tomato.bookstore.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserPrincipalTest {
  @Test
  @DisplayName("创建用户主体并获取用户 ID")
  void createUserPrincipalAndGetUserId() {
    // 准备
    String username = "testuser";
    String password = "password";
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    Long userId = 1L;

    // 执行
    UserPrincipal userPrincipal = new UserPrincipal(username, password, authorities, userId);

    // 验证
    assertEquals(username, userPrincipal.getUsername());
    assertEquals(password, userPrincipal.getPassword());
    assertEquals(authorities.size(), userPrincipal.getAuthorities().size());
    assertTrue(
        userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
    assertEquals(userId, userPrincipal.getUserId());
  }
}
