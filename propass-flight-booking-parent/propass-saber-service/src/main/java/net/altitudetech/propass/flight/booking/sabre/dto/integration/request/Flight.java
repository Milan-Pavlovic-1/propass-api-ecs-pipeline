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
public class Flight {
  @JsonProperty("Airline")
  private Airline airline;
  @JsonProperty("Number")
  private Integer number;
  @JsonProperty("DepartureDateTime")
  private String departureDateTime;
  @JsonProperty("ArrivalDateTime")
  private String arrivalDateTime;
  @JsonProperty("Type")
  private String type;
  @JsonProperty("ClassOfService")
  private String classOfService;
  @JsonProperty("OriginLocation")
  private OriginLocation originLocation;
  @JsonProperty("DestinationLocation")
  private DestinationLocation destinationLocation;
  
}
