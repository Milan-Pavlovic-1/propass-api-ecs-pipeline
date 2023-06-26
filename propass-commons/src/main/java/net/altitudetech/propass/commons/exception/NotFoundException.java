package net.altitudetech.propass.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends BaseException {

  private static final long serialVersionUID = 2692782582388685323L;

  public NotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND.value());
  }

}
