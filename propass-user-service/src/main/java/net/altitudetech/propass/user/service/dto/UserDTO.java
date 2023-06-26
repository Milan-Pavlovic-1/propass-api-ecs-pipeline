package net.altitudetech.propass.user.service.dto;

import lombok.Getter;
import lombok.Setter;
import net.altitudetech.propass.commons.dto.BaseDTO;

@Getter
@Setter
public class UserDTO extends BaseDTO {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
}
