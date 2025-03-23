package com.tomato.bookstore.service;

import com.tomato.bookstore.dto.LoginDTO;
import com.tomato.bookstore.dto.RegisterDTO;
import com.tomato.bookstore.dto.UserDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UserService {
  /**
   * 注册新用户
   *
   * @param registerDTO 注册信息
   * @throws BusinessException 用户名或邮箱已存在
   */
  @Transactional
  public void register(RegisterDTO registerDTO);

  /**
   * 用户登录
   *
   * @param loginDTO 登录信息
   * @return JWT token
   * @throws BusinessException 用户不存在
   */
  public String login(LoginDTO loginDTO);

  /**
   * 获取当前登录用户信息
   *
   * @param userId 用户ID
   * @return 当前登录用户信息
   * @throws ResourceNotFoundException 用户不存在
   */
  public UserDTO getCurrentUser(Long userId);

  /**
   * 更新用户信息
   *
   * @param userId 用户ID
   * @param userDTO 用户信息
   * @return 更新后的用户信息
   * @throws ResourceNotFoundException 用户不存在
   */
  @Transactional
  public UserDTO updateUser(Long userId, UserDTO userDTO);
}
