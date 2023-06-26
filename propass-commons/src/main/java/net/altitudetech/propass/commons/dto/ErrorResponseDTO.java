package net.altitudetech.propass.commons.dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDTO {
  private Date timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
}
