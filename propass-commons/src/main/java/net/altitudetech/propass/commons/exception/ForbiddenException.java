package net.altitudetech.propass.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends BaseException {
  private static final long serialVersionUID = -3828038632905197357L;

  public ForbiddenException(String message) {
    super(message, HttpStatus.FORBIDDEN.value());
  }

  public ForbiddenException(String message, Throwable throwable) {
    super(message, HttpStatus.FORBIDDEN.value(), throwable);
  }

}
