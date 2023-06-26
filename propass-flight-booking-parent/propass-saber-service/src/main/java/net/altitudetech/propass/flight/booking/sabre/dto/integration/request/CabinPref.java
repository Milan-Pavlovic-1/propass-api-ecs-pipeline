package net.altitudetech.propass.flight.booking.sabre.dto.integration.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "Cabin"
})
public class CabinPref {
  
  @JsonProperty("Cabin")
  private String cabin;
  
  public static CabinPref of(String cabin) {
    return CabinPref.builder().cabin(cabin).build();
  }
}
