package com.tomato.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册 DTO
 *
 * <p>该类用于接收注册请求的数据。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
  @NotBlank(message = "用户名不能为空")
  @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 个字符之间")
  private String username;

  @NotBlank(message = "密码不能为空")
  @Size(min = 6, max = 50, message = "密码长度必须在 6-50 个字符之间")
  private String password;

  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;

  @NotBlank(message = "手机号不能为空")
  @Size(min = 11, max = 11, message = "手机号长度必须为 11 位")
  private String phone;
}
