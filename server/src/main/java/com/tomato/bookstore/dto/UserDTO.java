package com.tomato.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  /** 密码字段，仅用于密码修改，不会在查询时返回 */
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;
}
