package com.tomato.bookstore.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tomato.bookstore.model.User;
import com.tomato.bookstore.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 自定义用户详细信息服务
 *
 * 该类实现了 Spring Security 的 {@link UserDetailsService} 接口，用于根据用户名加载用户详细信息。
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * 根据用户名加载用户详细信息
     *
     * 该方法从用户仓库中查找指定用户名的用户，并将其转换为 Spring Security 可用的 {@link UserDetails} 对象。
     * 如果找不到指定用户名的用户，则抛出 {@link UsernameNotFoundException} 异常。
     *
     * @param username 要查找的用户名
     * @return 包含用户详细信息的 {@link UserDetails} 对象
     * @throws UsernameNotFoundException 当指定的用户名不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
        return new UserPrincipal(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user),
                user.getId());
    }

    /**
     * 获取用户的权限信息
     *
     * 该方法将用户的角色转换为 Spring Security 可用的 {@link GrantedAuthority} 对象。
     *
     * @param user 要获取权限信息的用户
     * @return 包含用户权限信息的 {@link GrantedAuthority} 对象集合
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return authorities;
    }
}
