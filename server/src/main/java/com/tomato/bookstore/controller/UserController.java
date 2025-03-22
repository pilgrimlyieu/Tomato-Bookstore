package com.tomato.bookstore.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.dto.UserDTO;
import com.tomato.bookstore.security.UserPrincipal;
import com.tomato.bookstore.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 用户控制器
 *
 * 该类包含用户相关的接口。
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 获取用户信息
     *
     * @param principal 当前用户
     * @return 用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<UserDTO> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserDTO userDTO = userService.getCurrentUser(principal.getUserId());
        return ApiResponse.success(userDTO);
    }

    /**
     * 更新用户信息
     *
     * @param principal 当前用户
     * @param userDTO   用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/profile")
    public ApiResponse<UserDTO> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
                    @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(principal.getUserId(), userDTO);
        return ApiResponse.success(updatedUser);
    }
}
