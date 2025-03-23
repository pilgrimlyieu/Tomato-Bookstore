package com.tomato.bookstore.exception;

/**
 * 资源不存在异常
 *
 * <p>用于表示请求的资源不存在
 */
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }

  /**
   * 创建资源不存在异常
   *
   * @param resourceName 资源名称
   * @param fieldName 字段名称
   * @param fieldValue 字段值
   * @return 资源不存在异常
   */
  public static ResourceNotFoundException create(
      String resourceName, String fieldName, Object fieldValue) {
    return new ResourceNotFoundException(
        String.format("%s 不存在（%s=%s）", resourceName, fieldName, fieldValue));
  }
}
