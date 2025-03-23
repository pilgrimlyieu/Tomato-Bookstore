package com.tomato.bookstore.exception;

import com.tomato.bookstore.constant.BusinessErrorCode;

/**
 * 业务异常
 *
 * <p>用于表示业务逻辑错误，而非系统错误
 */
public class BusinessException extends RuntimeException {
  private final BusinessErrorCode errorCode;

  public BusinessException(BusinessErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BusinessException(BusinessErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public BusinessException(BusinessErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public BusinessException(BusinessErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public BusinessErrorCode getErrorCode() {
    return errorCode;
  }
}
