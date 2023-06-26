package net.altitudetech.propass.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends BaseException {
  private static final long serialVersionUID = -6663150673930610157L;

  public InternalServerErrorException(String message) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  public InternalServerErrorException(String message, Throwable throwable) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), throwable);
  }
}
