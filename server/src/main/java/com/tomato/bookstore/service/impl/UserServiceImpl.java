package com.tomato.bookstore.service.impl;

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
import com.tomato.bookstore.service.UserService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  @Transactional
  public void register(RegisterDTO registerDTO) {
    // 检查用户名是否已存在
    if (userRepository.existsByUsername(registerDTO.getUsername())) {
      throw new BusinessException(BusinessErrorCode.USERNAME_ALREADY_EXISTS);
    }
    // 检查邮箱是否已存在
    if (userRepository.existsByEmail(registerDTO.getEmail())) {
      throw new BusinessException(BusinessErrorCode.EMAIL_ALREADY_EXISTS);
    }
    if (userRepository.existsByPhone(registerDTO.getPhone())) {
      throw new BusinessException(BusinessErrorCode.PHONE_ALREADY_EXISTS);
    }
    // 创建用户实体
    LocalDateTime now = LocalDateTime.now();
    User user =
        User.builder()
            .username(registerDTO.getUsername())
            .password(passwordEncoder.encode(registerDTO.getPassword()))
            .email(registerDTO.getEmail())
            .phone(registerDTO.getPhone())
            .role(UserRole.CUSTOMER) // 默认为顾客角色
            .createdAt(now)
            .updatedAt(now)
            .build();
    // 保存用户
    userRepository.save(user);
  }

  public String login(LoginDTO loginDTO) {
    // 使用 Spring Security 进行认证
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    // 获取用户信息
    User user =
        userRepository
            .findByUsername(loginDTO.getUsername())
            .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));
    // 生成 JWT token
    return jwtTokenProvider.generateToken(user.getUsername(), user.getId());
  }

  public UserDTO getCurrentUser(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在，ID：" + userId));

    return convertToDTO(user);
  }

  @Transactional
  public UserDTO updateUser(Long userId, UserDTO userDTO) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在，ID：" + userId));
    // 更新用户信息
    if (userDTO.getEmail() != null) {
      if (userRepository.existsByEmail(userDTO.getEmail())) {
        throw new BusinessException(BusinessErrorCode.EMAIL_ALREADY_EXISTS);
      }
      user.setEmail(userDTO.getEmail());
    }
    if (userDTO.getPhone() != null) {
      if (userRepository.existsByPhone(userDTO.getPhone())) {
        throw new BusinessException(BusinessErrorCode.PHONE_ALREADY_EXISTS);
      }
      user.setPhone(userDTO.getPhone());
    }
    if (userDTO.getAddress() != null) {
      user.setAddress(userDTO.getAddress());
    }
    // 更新头像
    if (userDTO.getAvatar() != null) {
      user.setAvatar(userDTO.getAvatar());
    }
    // 更新密码
    if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
      user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    }
    user.setUpdatedAt(LocalDateTime.now());

    userRepository.save(user);
    return convertToDTO(user);
  }

  /**
   * 将 User 实体转换为 UserDTO
   *
   * @param user User 实体
   * @return UserDTO 对象
   */
  private UserDTO convertToDTO(User user) {
    return UserDTO.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .phone(user.getPhone())
        .avatar(user.getAvatar())
        .address(user.getAddress())
        .role(user.getRole())
        .build();
  }
}
