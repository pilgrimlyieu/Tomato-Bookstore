package com.tomato.bookstore.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tomato.bookstore.constant.RoleConstants;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.model.User.UserRole;
import com.tomato.bookstore.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
  @Mock private UserRepository userRepository;

  @InjectMocks private CustomUserDetailsService userDetailsService;

  private User user;

  @BeforeEach
  void setUp() {
    user =
        User.builder()
            .id(1L)
            .username("testuser")
            .password("password")
            .email("test@example.com")
            .phone("13800138000")
            .role(UserRole.CUSTOMER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("成功加载用户详情")
  void loadUserByUsernameSuccess() {
    // 准备
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    // 执行
    UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

    // 验证
    assertNotNull(userDetails);
    assertEquals("testuser", userDetails.getUsername());
    assertEquals("password", userDetails.getPassword());
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(RoleConstants.ROLE_CUSTOMER)));
    assertTrue(userDetails instanceof UserPrincipal);
    assertEquals(1L, ((UserPrincipal) userDetails).getUserId());

    verify(userRepository).findByUsername("testuser");
  }

  @Test
  @DisplayName("用户不存在时抛出异常")
  void loadUserByUsernameUserNotFound() {
    // 准备
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    // 执行和验证
    UsernameNotFoundException exception =
        assertThrows(
            UsernameNotFoundException.class,
            () -> {
              userDetailsService.loadUserByUsername("nonexistent");
            });

    assertTrue(exception.getMessage().contains("用户不存在"));
    verify(userRepository).findByUsername("nonexistent");
  }

  @Test
  @DisplayName("管理员用户权限检查")
  void loadAdminUserAuthorities() {
    // 准备
    user.setRole(UserRole.ADMIN);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

    // 执行
    UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

    // 验证
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(RoleConstants.ROLE_ADMIN)));
    verify(userRepository).findByUsername("admin");
  }
}
