package net.altitudetech.propass.commons.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
  private static final long serialVersionUID = 6681798709534097887L;
  private final int statusCode;

  public BaseException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public BaseException(String message, int statusCode, Throwable throwable) {
    super(message, throwable);
    this.statusCode = statusCode;
  }

  protected Integer getErrorCode() {
    return this.statusCode;
  }
}
