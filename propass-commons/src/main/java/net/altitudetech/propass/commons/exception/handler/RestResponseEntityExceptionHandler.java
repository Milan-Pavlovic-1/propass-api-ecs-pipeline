package net.altitudetech.propass.commons.exception.handler;

import java.sql.SQLException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import net.altitudetech.propass.commons.exception.BaseException;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({SQLException.class, DataAccessException.class})
  protected ResponseEntity<Object> handleDataIntegrityViolation(Exception ex, WebRequest request) {
    log.error("DB error.", ex);
    // get different status code for easier tracking
    return wrapAndHandle(ex, "Something went wrong.", new HttpHeaders(),
        HttpStatus.SERVICE_UNAVAILABLE, request);
  }

  @ExceptionHandler({AccessDeniedException.class})
  protected ResponseEntity<Object> handleAccessDenied(Exception ex, WebRequest request) {
    return wrapAndHandle(ex, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
  }

  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleUnhandledException(Exception ex, WebRequest request)
      throws Exception {
    // if the exception is annotated with @ResponseStatus rethrow it and let Spring
    // handle it as it is, most likely, intentional
    if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null) {
      throw ex;
    }
    log.error("Internal error.", ex);
    return wrapAndHandle(ex, "Something went wrong. Please try again later.", new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @SuppressWarnings("serial")
  private ResponseEntity<Object> wrapAndHandle(Exception ex, String body, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    return handleExceptionInternal(new BaseException(body, status.value(), ex) {}, body, headers,
        status, request);
  }
}
