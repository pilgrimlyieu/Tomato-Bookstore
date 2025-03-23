package com.tomato.bookstore.dto;

import com.tomato.bookstore.constant.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(HttpStatus.OK.getCode(), HttpStatus.OK.getDefaultMessage(), data);
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(HttpStatus.OK.getCode(), message, data);
  }

  public static <T> ApiResponse<T> created(T data) {
    return new ApiResponse<>(
        HttpStatus.CREATED.getCode(), HttpStatus.CREATED.getDefaultMessage(), data);
  }

  public static ApiResponse<Void> success() {
    return new ApiResponse<>(HttpStatus.OK.getCode(), HttpStatus.OK.getDefaultMessage(), null);
  }

  public static ApiResponse<Void> fail(int code, String message) {
    return new ApiResponse<>(code, message, null);
  }

  public static ApiResponse<Void> badRequest(String message) {
    return new ApiResponse<>(HttpStatus.BAD_REQUEST.getCode(), message, null);
  }

  public static ApiResponse<Void> unauthorized(String message) {
    return new ApiResponse<>(HttpStatus.UNAUTHORIZED.getCode(), message, null);
  }

  public static ApiResponse<Void> forbidden(String message) {
    return new ApiResponse<>(HttpStatus.FORBIDDEN.getCode(), message, null);
  }

  public static ApiResponse<Void> notFound(String message) {
    return new ApiResponse<>(HttpStatus.NOT_FOUND.getCode(), message, null);
  }

  public static ApiResponse<Void> serverError(String message) {
    return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), message, null);
  }
}
