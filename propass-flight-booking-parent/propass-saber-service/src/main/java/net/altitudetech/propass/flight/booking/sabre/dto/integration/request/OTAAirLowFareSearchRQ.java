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
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "Version",
  "POS",
  "OriginDestinationInformation",
  "TravelPreferences",
  "TravelerInfoSummary"
})
public class OTAAirLowFareSearchRQ {
  
  @JsonProperty("Version")
  private String version;
  @JsonProperty("POS")
  private Pos pos;
  @JsonProperty("OriginDestinationInformation")
  private List<OriginDestinationInformation> originDestinationInformation;
  @JsonProperty("TravelPreferences")
  private TravelPreferences travelPreferences;
  @JsonProperty("TravelerInfoSummary")
  private TravelerInfoSummary travelerInfoSummary;
}
