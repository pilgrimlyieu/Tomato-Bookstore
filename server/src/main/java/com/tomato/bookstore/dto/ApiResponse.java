package com.tomato.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * API 响应
 *
 * <p>该类用于封装 API 的响应结果。
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
  private int code;
  private String message;
  private T data;

  // 标准消息常量
  public static final String MESSAGE_SUCCESS = "操作成功";
  public static final String MESSAGE_CREATED = "创建成功";
  public static final String MESSAGE_BAD_REQUEST = "请求参数错误";
  public static final String MESSAGE_UNAUTHORIZED = "未授权访问";
  public static final String MESSAGE_FORBIDDEN = "禁止访问";
  public static final String MESSAGE_NOT_FOUND = "资源不存在";
  public static final String MESSAGE_SERVER_ERROR = "服务器内部错误";

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(HttpStatus.OK.value(), MESSAGE_SUCCESS, data);
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(HttpStatus.OK.value(), message, data);
  }

  public static <T> ApiResponse<T> created(T data) {
    return new ApiResponse<>(HttpStatus.CREATED.value(), MESSAGE_CREATED, data);
  }

  public static ApiResponse<Void> success() {
    return new ApiResponse<>(HttpStatus.OK.value(), MESSAGE_SUCCESS, null);
  }

  public static ApiResponse<Void> fail(int code, String message) {
    return new ApiResponse<>(code, message, null);
  }

  public static ApiResponse<Void> badRequest(String message) {
    return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), message, null);
  }

  public static ApiResponse<Void> unauthorized(String message) {
    return new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), message, null);
  }

  public static ApiResponse<Void> forbidden(String message) {
    return new ApiResponse<>(HttpStatus.FORBIDDEN.value(), message, null);
  }

  public static ApiResponse<Void> notFound(String message) {
    return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), message, null);
  }

  public static ApiResponse<Void> serverError(String message) {
    return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
  }
}
