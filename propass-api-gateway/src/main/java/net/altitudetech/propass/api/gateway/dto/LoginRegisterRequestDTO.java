package net.altitudetech.propass.api.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRegisterRequestDTO {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
}
