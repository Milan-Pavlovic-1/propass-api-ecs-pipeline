package net.altitudetech.propass.api.gateway.dto;

import org.springframework.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightLocationDTO {
  @NonNull
  private String name;
  @NonNull
  private String code;
  @NonNull
  private String type;
}
