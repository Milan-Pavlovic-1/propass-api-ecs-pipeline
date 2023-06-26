package net.altitudetech.propass.flight.booking.sabre.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevalidateFlightRequestDTO extends BaseDTO {
  
  @JsonProperty("departureDateTime")
  private String departureDateTime;
  @JsonProperty("originLocationCode")
  private String originLocationCode;
  @JsonProperty("destinationLocationCode")
  private String destinationLocationCode;
  @JsonProperty("passengers")
  private List<Passenger> passengers;
  @JsonProperty("flightSegments")
  private List<FlightSegment> flightSegments;
  
  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class FlightSegment {
    @JsonProperty("number")
    private Integer number;
    @JsonProperty("type")
    private String type;
    @JsonProperty("departureDateTime")
    private String departureDateTime;
    @JsonProperty("arrivalDateTime")
    private String arrivalDateTime;
    @JsonProperty("classOfService")
    private String classOfService;
    @JsonProperty("originLocation")
    private String originLocation;
    @JsonProperty("destinationLocation")
    private String destinationLocation;
  }
}
