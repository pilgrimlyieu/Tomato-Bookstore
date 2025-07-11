package com.tomato.bookstore.controller;

import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.dto.LoginDTO;
import com.tomato.bookstore.dto.RegisterDTO;
import com.tomato.bookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户认证控制器
 *
 * <p>该类包含用户认证相关的接口。
 */
@RestController
@RequestMapping(ApiConstants.USER_BASE_PATH)
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;

  /**
   * 用户注册
   *
   * @param registerDTO 注册信息
   * @return 注册结果
   */
  @PostMapping(ApiConstants.USER_REGISTER)
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
    userService.register(registerDTO);
    return ApiResponse.created(null);
  }

  /**
   * 用户登录
   *
   * @param loginDTO 登录信息
   * @return 登录结果
   */
  @PostMapping(ApiConstants.USER_LOGIN)
  public ApiResponse<String> login(@Valid @RequestBody LoginDTO loginDTO) {
    String token = userService.login(loginDTO);
    return ApiResponse.success("登录成功", token);
  }
}
