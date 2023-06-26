package net.altitudetech.propass.flight.booking.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.dto.BaseDTO;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFlightRequestDTO extends BaseDTO {
  @JsonProperty("roundTrip")
  private Boolean roundTrip;
  @JsonProperty("departureDateTime")
  private String departureDateTime;
  @JsonProperty("returnDateTime")
  private String returnDateTime;
  @JsonProperty("originLocationCode")
  private String originLocationCode;
  @JsonProperty("destinationLocationCode")
  private String destinationLocationCode;
  @JsonProperty("cabin")
  private String cabin;
  @JsonProperty("passengers")
  private List<Passenger> passengers;
  
}
