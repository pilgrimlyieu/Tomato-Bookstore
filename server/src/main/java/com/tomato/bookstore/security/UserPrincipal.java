package com.tomato.bookstore.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

/**
 * 用户主体
 *
 * 该类继承自 {@link User}，并添加了用户 ID 属性。
 */
public class UserPrincipal extends User {
    @Getter
    private final Long userId;

    /**
     * 构造一个 {@link UserPrincipal} 对象
     *
     * @param username    用户名
     * @param password    密码
     * @param authorities 用户权限
     * @param userId      用户 ID
     */
    public UserPrincipal(String username, String password,
            Collection<? extends GrantedAuthority> authorities,
            Long userId) {
        super(username, password, authorities);
        this.userId = userId;
    }
}
