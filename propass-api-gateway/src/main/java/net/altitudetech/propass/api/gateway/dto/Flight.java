package net.altitudetech.propass.api.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
  
  @JsonProperty("duration")
  private Integer duration;
  @JsonProperty("directFlight")
  private Boolean directFlight;
  @JsonProperty("departureDate")
  private String departureDate;
  @Singular
  @JsonProperty("segments")
  private List<FlightSegment> segments;
  
}
