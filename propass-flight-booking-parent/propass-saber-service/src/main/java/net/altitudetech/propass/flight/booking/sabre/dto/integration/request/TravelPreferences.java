package net.altitudetech.propass.flight.booking.sabre.dto.integration.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "TPA_Extensions"
})
public class TravelPreferences {
  
  @JsonProperty("VendorPref")
  private List<VendorPref> vendorPrefs;
  @JsonProperty("TPA_Extensions")
  private TPAExtensions tPAExtensions;
  
  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
          "NumTrips",
          "DataSources"
  })
  public static class TPAExtensions {
    
    @JsonProperty("NumTrips")
    private NumTrips numTrips;
    @JsonProperty("DataSources")
    private DataSources dataSources;
  }
}
