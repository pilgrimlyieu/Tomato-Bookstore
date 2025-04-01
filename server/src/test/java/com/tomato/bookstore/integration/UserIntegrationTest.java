package com.tomato.bookstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.LoginDTO;
import com.tomato.bookstore.dto.RegisterDTO;
import com.tomato.bookstore.dto.UserDTO;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.model.User.UserRole;
import com.tomato.bookstore.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  private RegisterDTO registerDTO;
  private LoginDTO loginDTO;

  @BeforeEach
  void setUp() {
    // 清理数据库中的测试用户
    userRepository.deleteAll();

    // 初始化测试数据
    registerDTO = new RegisterDTO();
    registerDTO.setUsername("integrationtest");
    registerDTO.setPassword("password123");
    registerDTO.setEmail("integration@example.com");
    registerDTO.setPhone("13800138000");

    loginDTO = new LoginDTO();
    loginDTO.setUsername("integrationtest");
    loginDTO.setPassword("password123");

    // 创建一个已存在的用户用于测试
    User existingUser =
        User.builder()
            .username("existinguser")
            .password(
                "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // 'password' 加密后
            .email("existing@example.com")
            .phone("13900139000")
            .role(UserRole.CUSTOMER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    userRepository.save(existingUser);
  }

  @Test
  @DisplayName("完整用户流程 - 注册、登录和获取信息")
  void completeUserFlow() throws Exception {
    // 1. 注册
    mockMvc
        .perform(
            post(ApiConstants.USER_REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()));

    // 验证用户已创建
    assertTrue(userRepository.existsByUsername("integrationtest"));

    // 2. 登录
    MvcResult loginResult =
        mockMvc
            .perform(
                post(ApiConstants.USER_LOGIN_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.msg").value("登录成功"))
            .andExpect(jsonPath("$.data").isString())
            .andReturn();

    // 提取 token
    String responseJson = loginResult.getResponse().getContentAsString();
    String token = objectMapper.readTree(responseJson).get("data").asText();
    assertNotNull(token);

    // 3. 获取用户信息
    mockMvc
        .perform(get(ApiConstants.USER_PROFILE_PATH).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.username").value("integrationtest"))
        .andExpect(jsonPath("$.data.email").value("integration@example.com"));

    // 4. 更新用户信息
    UserDTO updateDTO = new UserDTO();
    updateDTO.setEmail("updated@example.com");
    updateDTO.setPhone("13866668888");

    mockMvc
        .perform(
            put(ApiConstants.USER_PROFILE_PATH)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.email").value("updated@example.com"))
        .andExpect(jsonPath("$.data.phone").value("13866668888"));

    // 验证数据库中已更新
    User updatedUser = userRepository.findByUsername("integrationtest").orElseThrow();
    assertEquals("updated@example.com", updatedUser.getEmail());
    assertEquals("13866668888", updatedUser.getPhone());
  }

  @Test
  @DisplayName("注册失败 - 用户名已存在")
  void registerFailsWhenUsernameExists() throws Exception {
    // 复用已存在用户的用户名
    registerDTO.setUsername("existinguser");

    mockMvc
        .perform(
            post(ApiConstants.USER_REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(BusinessErrorCode.USERNAME_ALREADY_EXISTS.getCode()))
        .andExpect(jsonPath("$.msg").value(BusinessErrorCode.USERNAME_ALREADY_EXISTS.getMessage()));
  }

  @Test
  @DisplayName("登录失败 - 密码错误")
  void loginFailsWithWrongPassword() throws Exception {
    // 使用错误的密码
    LoginDTO wrongLoginDTO = new LoginDTO();
    wrongLoginDTO.setUsername("existinguser");
    wrongLoginDTO.setPassword("wrongpassword");

    mockMvc
        .perform(
            post(ApiConstants.USER_LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongLoginDTO)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("未认证用户不能访问受保护的资源")
  void unauthenticatedAccessDenied() throws Exception {
    mockMvc.perform(get(ApiConstants.USER_PROFILE_PATH)).andExpect(status().isUnauthorized());
  }
}
