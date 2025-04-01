package com.tomato.bookstore.exception;

import com.tomato.bookstore.constant.BusinessErrorCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * <p>用于表示业务逻辑错误，而非系统错误
 */
@Getter
public class BusinessException extends RuntimeException {
  private final int code;
  private final BusinessErrorCode errorCode;

  /**
   * 创建业务异常
   *
   * @param errorCode 错误码枚举
   */
  public BusinessException(BusinessErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
    this.errorCode = errorCode;
  }

  /**
   * 创建业务异常
   *
   * @param errorCode 错误码枚举
   * @param message 自定义错误消息
   */
  public BusinessException(BusinessErrorCode errorCode, String message) {
    super(message);
    this.code = errorCode.getCode();
    this.errorCode = errorCode;
  }

  public BusinessException(BusinessErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.code = errorCode.getCode();
    this.errorCode = errorCode;
  }

  public BusinessException(BusinessErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.code = errorCode.getCode();
    this.errorCode = errorCode;
  }

  public BusinessErrorCode getErrorCode() {
    return errorCode;
  }
}
