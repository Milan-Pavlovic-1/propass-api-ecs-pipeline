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
  "CodeContext",
  "LocationCode"
})
public class DestinationLocation {
  
  @JsonProperty("CodeContext")
  private String codeContext;
  @JsonProperty("LocationCode")
  private String locationCode;
  
  public static DestinationLocation of(String codeContext, String locationCode) {
    return builder().codeContext(codeContext).locationCode(locationCode).build();
  }
  
  public static DestinationLocation of(String locationCode) {
    return of(null, locationCode);
  }
}
