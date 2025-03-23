package com.tomato.bookstore.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.dto.LoginDTO;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class GlobalExceptionHandlerTest {
  private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

  @Test
  @DisplayName("处理业务异常")
  void handleBusinessException() {
    // 准备
    BusinessException exception = new BusinessException(BusinessErrorCode.USERNAME_ALREADY_EXISTS);

    // 执行
    ResponseEntity<ApiResponse<Void>> response =
        exceptionHandler.handleBusinessException(exception);

    // 验证
    assertNotNull(response);
    ApiResponse<Void> body = response.getBody();
    assertNotNull(body);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(BusinessErrorCode.USERNAME_ALREADY_EXISTS.getCode(), body.getCode());
    assertEquals(BusinessErrorCode.USERNAME_ALREADY_EXISTS.getMessage(), body.getMessage());
  }

  @Test
  @DisplayName("处理资源不存在异常")
  void handleResourceNotFoundException() {
    // 准备
    ResourceNotFoundException exception = new ResourceNotFoundException("用户不存在");

    // 执行
    ResponseEntity<ApiResponse<Void>> response =
        exceptionHandler.handleResourceNotFoundException(exception);

    // 验证
    assertNotNull(response);
    ApiResponse<Void> body = response.getBody();
    assertNotNull(body);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(HttpStatus.NOT_FOUND.value(), body.getCode());
    assertEquals("用户不存在", body.getMessage());
  }

  @Test
  @DisplayName("处理认证异常")
  void handleAuthenticationException() {
    // 准备
    BadCredentialsException exception = new BadCredentialsException("Bad credentials");

    // 执行
    ResponseEntity<ApiResponse<Void>> response =
        exceptionHandler.handleAuthenticationException(exception);

    // 验证
    assertNotNull(response);
    ApiResponse<Void> body = response.getBody();
    assertNotNull(body);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(HttpStatus.UNAUTHORIZED.value(), body.getCode());
    assertEquals("Bad credentials", body.getMessage());
  }

  @Test
  @DisplayName("处理授权异常")
  void handleAccessDeniedException() {
    // 准备
    AccessDeniedException exception = new AccessDeniedException("Access is denied");

    // 执行
    ResponseEntity<ApiResponse<Void>> response =
        exceptionHandler.handleAccessDeniedException(exception);

    // 验证
    assertNotNull(response);
    ApiResponse<Void> body = response.getBody();
    assertNotNull(body);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals(HttpStatus.FORBIDDEN.value(), body.getCode());
    assertEquals("Access is denied", body.getMessage());
  }

  @Test
  @DisplayName("处理参数验证异常")
  void handleValidationExceptions() throws Exception {
    // 准备 - 创建一个有效的 MethodParameter 对象
    MethodParameter parameter =
        new MethodParameter(LoginDTO.class.getDeclaredMethod("getUsername"), -1);

    // 创建一个 BeanPropertyBindingResult
    BeanPropertyBindingResult bindingResult =
        new BeanPropertyBindingResult(new Object(), "objectName");
    bindingResult.addError(new FieldError("objectName", "username", "用户名不能为空"));
    bindingResult.addError(new FieldError("objectName", "password", "密码不能为空"));

    // 创建 MethodArgumentNotValidException
    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(parameter, bindingResult);

    // 执行
    ResponseEntity<ApiResponse<Map<String, String>>> response =
        exceptionHandler.handleValidationExceptions(exception);

    // 验证
    assertNotNull(response);
    ApiResponse<Map<String, String>> body = response.getBody();
    assertNotNull(body);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(HttpStatus.BAD_REQUEST.value(), body.getCode());
    assertEquals(ApiResponse.MESSAGE_BAD_REQUEST, body.getMessage());

    assertNotNull(body.getData());
    Map<String, String> errors = body.getData();
    assertEquals(2, errors.size());
    assertEquals("用户名不能为空", errors.get("username"));
    assertEquals("密码不能为空", errors.get("password"));
  }

  @Test
  @DisplayName("处理未捕获的异常")
  void handleGlobalException() {
    // 准备
    Exception exception = new RuntimeException("Unexpected error");

    // 执行
    ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleGlobalException(exception);

    // 验证
    assertNotNull(response);
    ApiResponse<Void> body = response.getBody();
    assertNotNull(body);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getCode());
    assertTrue(body.getMessage().contains(ApiResponse.MESSAGE_SERVER_ERROR));
    assertTrue(body.getMessage().contains("Unexpected error"));
  }
}
