package com.tomato.bookstore.dto;

import com.tomato.bookstore.model.User.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户 DTO
 *
 * <p>该类用于返回用户信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
  private Long id;
  private String username;
  private String email;
  private String phone;
  private String avatar;
  private String address;
  private UserRole role;
}
