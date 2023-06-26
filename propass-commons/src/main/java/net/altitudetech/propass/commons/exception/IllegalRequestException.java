package net.altitudetech.propass.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalRequestException extends BaseException {

  private static final long serialVersionUID = -8470841105946581259L;

  public IllegalRequestException(String message) {
    super(message, HttpStatus.BAD_REQUEST.value());
  }

}
