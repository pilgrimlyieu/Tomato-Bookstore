package com.tomato.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.LoginDTO;
import com.tomato.bookstore.dto.RegisterDTO;
import com.tomato.bookstore.dto.UserDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.model.User.UserRole;
import com.tomato.bookstore.repository.UserRepository;
import com.tomato.bookstore.security.JwtTokenProvider;
import com.tomato.bookstore.service.impl.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtTokenProvider jwtTokenProvider;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private Authentication authentication;

  @InjectMocks private UserServiceImpl userService;

  private RegisterDTO registerDTO;
  private LoginDTO loginDTO;
  private User user;

  @BeforeEach
  void setUp() {
    // 设置测试数据
    registerDTO = new RegisterDTO();
    registerDTO.setUsername("testuser");
    registerDTO.setPassword("password123");
    registerDTO.setEmail("test@example.com");
    registerDTO.setPhone("13800138000");

    loginDTO = new LoginDTO();
    loginDTO.setUsername("testuser");
    loginDTO.setPassword("password123");

    user =
        User.builder()
            .id(1L)
            .username("testuser")
            .password("encodedPassword")
            .email("test@example.com")
            .phone("13800138000")
            .role(UserRole.CUSTOMER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("注册成功")
  void registerSuccess() {
    // 准备
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

    // 执行
    userService.register(registerDTO);

    // 验证
    verify(userRepository).existsByUsername("testuser");
    verify(userRepository).existsByEmail("test@example.com");
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("注册失败 - 用户名已存在")
  void registerFailsWhenUsernameExists() {
    // 准备
    when(userRepository.existsByUsername("testuser")).thenReturn(true);

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              userService.register(registerDTO);
            });

    assertEquals(BusinessErrorCode.USERNAME_ALREADY_EXISTS, exception.getErrorCode());
    assertEquals(BusinessErrorCode.USERNAME_ALREADY_EXISTS.getMessage(), exception.getMessage());
    verify(userRepository).existsByUsername("testuser");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("注册失败 - 邮箱已存在")
  void registerFailsWhenEmailExists() {
    // 准备
    when(userRepository.existsByUsername("testuser")).thenReturn(false);
    when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              userService.register(registerDTO);
            });

    assertEquals(BusinessErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    assertEquals(BusinessErrorCode.EMAIL_ALREADY_EXISTS.getMessage(), exception.getMessage());
    verify(userRepository).existsByUsername("testuser");
    verify(userRepository).existsByEmail("test@example.com");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("登录成功")
  void loginSuccess() {
    // 准备
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(jwtTokenProvider.generateToken("testuser", 1L)).thenReturn("jwt-token");

    // 执行
    String token = userService.login(loginDTO);

    // 验证
    assertEquals("jwt-token", token);
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userRepository).findByUsername("testuser");
    verify(jwtTokenProvider).generateToken("testuser", 1L);
  }

  @Test
  @DisplayName("登录失败 - 用户不存在")
  void loginFailsWhenUserNotFound() {
    // 准备
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    // 执行和验证
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              userService.login(loginDTO);
            });

    assertEquals(BusinessErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    assertEquals(BusinessErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userRepository).findByUsername("testuser");
    verify(jwtTokenProvider, never()).generateToken(anyString(), anyLong());
  }

  @Test
  @DisplayName("获取当前用户成功")
  void getCurrentUserSuccess() {
    // 准备
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    // 执行
    UserDTO result = userService.getCurrentUser(1L);

    // 验证
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("testuser", result.getUsername());
    assertEquals("test@example.com", result.getEmail());
    verify(userRepository).findById(1L);
  }

  @Test
  @DisplayName("获取当前用户失败 - 用户不存在")
  void getCurrentUserFailsWhenUserNotFound() {
    // 准备
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              userService.getCurrentUser(1L);
            });

    assertTrue(exception.getMessage().contains("用户不存在"));
    verify(userRepository).findById(1L);
  }

  @Test
  @DisplayName("更新用户成功")
  void updateUserSuccess() {
    // 准备
    UserDTO updateDTO = new UserDTO();
    updateDTO.setEmail("new@example.com");
    updateDTO.setPhone("13900139000");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    // 执行
    UserDTO result = userService.updateUser(1L, updateDTO);

    // 验证
    assertNotNull(result);
    verify(userRepository).findById(1L);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("更新用户失败 - 用户不存在")
  void updateUserFailsWhenUserNotFound() {
    // 准备
    UserDTO updateDTO = new UserDTO();
    updateDTO.setEmail("new@example.com");

    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    // 执行和验证
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> {
              userService.updateUser(1L, updateDTO);
            });

    assertTrue(exception.getMessage().contains("用户不存在"));
    verify(userRepository).findById(1L);
    verify(userRepository, never()).save(any(User.class));
  }
}
