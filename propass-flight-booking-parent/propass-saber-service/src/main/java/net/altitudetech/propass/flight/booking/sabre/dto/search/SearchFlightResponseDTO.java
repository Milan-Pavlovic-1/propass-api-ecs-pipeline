package net.altitudetech.propass.flight.booking.sabre.dto.search;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import net.altitudetech.propass.commons.dto.BaseDTO;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFlightResponseDTO extends BaseDTO {
  
  @Singular
  @JsonProperty("outboundFlights")
  private List<Flight> outboundFlights;
  @Singular
  @JsonProperty("returnFlights")
  private List<Flight> returnFlights;
  
}
