package net.altitudetech.propass.flight.booking.sabre.dto.integration.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "RPH",
  "DepartureDateTime",
  "OriginLocation",
  "DestinationLocation",
  "TPA_Extensions"
})
public class OriginDestinationInformation {
  
  @JsonProperty("RPH")
  private String rph;
  @JsonProperty("DepartureDateTime")
  private String departureDateTime;
  @JsonProperty("OriginLocation")
  private OriginLocation originLocation;
  @JsonProperty("DestinationLocation")
  private DestinationLocation destinationLocation;
  @JsonProperty("TPA_Extensions")
  private TPAExtensions tPAExtensions;
  
  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
    "CabinPref"
  })
  public static class TPAExtensions {
    
    @JsonProperty("CabinPref")
    private CabinPref cabinPref;
    @JsonProperty("Flight")
    private List<Flight> flights;
    
    
    public static TPAExtensions of(CabinPref cabinPref, List<Flight> flights) {
      return builder().cabinPref(cabinPref).flights(flights).build();
    }
    
    public static TPAExtensions of(List<Flight> flights) {
      return of(null, flights);
    }
    public static TPAExtensions of(CabinPref cabinPref) {
      return of(cabinPref, null);
    }
  }
}
