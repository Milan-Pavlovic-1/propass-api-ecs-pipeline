package net.altitudetech.propass.flight.booking.sabre.dto.integration.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Airline {
  @JsonProperty("Marketing")
  private String marketing;
  @JsonProperty("Operating")
  private String operating;
}
