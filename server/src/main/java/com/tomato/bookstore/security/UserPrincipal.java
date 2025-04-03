package com.tomato.bookstore.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** 扩展 Spring Security 的 User 类，增加用户 ID 字段 */
@Getter
public class UserPrincipal extends User {

  private final Long userId;

  /**
   * 创建 UserPrincipal 实例
   *
   * @param username 用户名
   * @param password 密码
   * @param authorities 权限集合
   * @param userId 用户 ID
   */
  public UserPrincipal(
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities,
      Long userId) {
    super(username, password, authorities);
    this.userId = userId;
  }

  /**
   * 创建 UserPrincipal 实例（完整参数）
   *
   * @param username 用户名
   * @param password 密码
   * @param enabled 账号是否启用
   * @param accountNonExpired 账号是否未过期
   * @param credentialsNonExpired 凭证是否未过期
   * @param accountNonLocked 账号是否未锁定
   * @param authorities 权限集合
   * @param userId 用户 ID
   */
  public UserPrincipal(
      String username,
      String password,
      boolean enabled,
      boolean accountNonExpired,
      boolean credentialsNonExpired,
      boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities,
      Long userId) {
    super(
        username,
        password,
        enabled,
        accountNonExpired,
        credentialsNonExpired,
        accountNonLocked,
        authorities);
    this.userId = userId;
  }
}
