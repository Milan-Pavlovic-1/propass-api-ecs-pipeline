package net.altitudetech.propass.flight.booking.sabre.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.ScheduleDesc;

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
  private List<ScheduleDesc> segments;
  
}
