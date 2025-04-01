package com.tomato.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * API 响应封装类
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
  private int code;
  private T data;
  private String msg;

  public static final String MESSAGE_SUCCESS = "操作成功";
  public static final String MESSAGE_CREATED = "创建成功";

  /**
   * 成功响应
   *
   * @param data 数据
   * @param <T> 数据类型
   * @return API 响应对象
   */
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(HttpStatus.OK.value(), data, MESSAGE_SUCCESS);
  }

  /**
   * 带消息的成功响应
   *
   * @param message 成功消息
   * @param data 数据
   * @param <T> 数据类型
   * @return API 响应对象
   */
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(HttpStatus.OK.value(), data, message);
  }

  /**
   * 创建成功响应
   *
   * @param data 数据
   * @param <T> 数据类型
   * @return API 响应对象
   */
  public static <T> ApiResponse<T> created(T data) {
    return new ApiResponse<>(HttpStatus.CREATED.value(), data, MESSAGE_CREATED);
  }

  /**
   * 错误响应
   *
   * @param code 错误码
   * @param message 错误消息
   * @param <T> 数据类型
   * @return API 响应对象
   */
  public static <T> ApiResponse<T> error(int code, String message) {
    return new ApiResponse<>(code, null, message);
  }

  /**
   * 带数据的错误响应
   *
   * @param code 错误码
   * @param message 错误消息
   * @param data 错误数据
   * @param <T> 数据类型
   * @return API 响应对象
   */
  public static <T> ApiResponse<T> error(int code, String message, T data) {
    return new ApiResponse<>(code, data, message);
  }
}
