package com.tomato.bookstore.exception;

import com.tomato.bookstore.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * <p>该类用于处理所有控制器抛出的异常，并返回统一格式的响应。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  /**
   * 处理资源不存在异常
   *
   * <p>该异常通常用于表示请求的资源不存在，例如用户、商品等。
   *
   * @param e 资源不存在异常
   * @return ApiResponse 统一格式的响应
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiResponse<Void> handleResourceNotFoundException(ResourceNotFoundException e) {
    log.warn("资源不存在：{}", e.getMessage());
    return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
  }

  /**
   * 处理业务异常
   *
   * <p>该异常通常用于表示业务逻辑错误，例如参数不合法、操作不允许等。
   *
   * @param e 业务异常
   * @return ApiResponse 统一格式的响应
   */
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> handleBusinessException(BusinessException e) {
    log.warn("业务异常：{} - {}", e.getCode(), e.getMessage());
    return ApiResponse.error(e.getCode(), e.getMessage());
  }

  /**
   * 处理参数校验异常（表单提交）
   *
   * <p>该异常通常用于表示表单提交时的参数校验错误。例如：必填字段未填写、格式不正确等。
   *
   * @param e 参数校验异常
   * @return ApiResponse 统一格式的响应
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    log.warn("参数校验不通过：{}", errors);
    return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "参数校验不通过", errors);
  }

  /**
   * 处理参数校验异常（路径参数）
   *
   * <p>该异常通常用于表示路径参数的校验错误。例如：ID 格式不正确等。
   *
   * @param e 参数校验异常
   * @return ApiResponse 统一格式的响应
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> handleConstraintViolationException(ConstraintViolationException e) {
    String message =
        e.getConstraintViolations().stream()
            .map(violation -> violation.getMessage())
            .collect(Collectors.joining(", "));
    log.warn("参数校验不通过：{}", message);
    return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message);
  }

  /**
   * 处理权限不足异常
   *
   * <p>该异常通常用于表示用户没有权限访问请求的资源。例如：访问受保护的 API 接口等。
   *
   * @param e 权限不足异常
   * @return ApiResponse 统一格式的响应
   */
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException e) {
    log.warn("权限不足：{}", e.getMessage());
    return ApiResponse.error(HttpStatus.FORBIDDEN.value(), "权限不足，无法访问");
  }

  /**
   * 处理认证异常
   *
   * <p>该异常通常用于表示用户认证失败，例如：JWT 过期、无效等。
   *
   * @param e 认证异常
   * @return ApiResponse 统一格式的响应
   */
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiResponse<Void> handleAuthenticationException(AuthenticationException e) {
    log.warn("认证失败：{}", e.getMessage());
    return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "认证失败：" + e.getMessage());
  }

  /**
   * 处理其他所有异常
   *
   * <p>该异常通常用于表示系统内部错误，例如：数据库连接失败、网络异常等。
   *
   * @param e 其他异常
   * @return ApiResponse 统一格式的响应
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<Void> handleException(Exception e) {
    log.error("系统异常", e);
    return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统异常，请稍后再试");
  }
}
