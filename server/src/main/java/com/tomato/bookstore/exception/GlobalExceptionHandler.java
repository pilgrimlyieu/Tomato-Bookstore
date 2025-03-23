package com.tomato.bookstore.exception;

import com.tomato.bookstore.dto.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  /**
   * 处理业务异常
   *
   * @param ex 业务异常
   * @return 响应实体
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
    ApiResponse<Void> response = ApiResponse.fail(ex.getErrorCode().getCode(), ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理资源不存在异常
   *
   * @param ex 资源不存在异常
   * @return 响应实体
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    return new ResponseEntity<>(ApiResponse.notFound(ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  /**
   * 处理请求参数验证异常
   *
   * @param ex 请求参数验证异常
   * @return 响应实体
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    ApiResponse<Map<String, String>> response =
        new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ApiResponse.MESSAGE_BAD_REQUEST, errors);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理认证异常
   *
   * @param ex 认证异常
   * @return 响应实体
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
      AuthenticationException ex) {
    return new ResponseEntity<>(ApiResponse.unauthorized(ex.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  /**
   * 处理授权异常
   *
   * @param ex 授权异常
   * @return 响应实体
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
    return new ResponseEntity<>(ApiResponse.forbidden(ex.getMessage()), HttpStatus.FORBIDDEN);
  }

  /**
   * 处理所有其他未捕获的异常
   *
   * @param ex 未捕获的异常
   * @return 响应实体
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
    return new ResponseEntity<>(
        ApiResponse.serverError(ApiResponse.MESSAGE_SERVER_ERROR + "：" + ex.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
