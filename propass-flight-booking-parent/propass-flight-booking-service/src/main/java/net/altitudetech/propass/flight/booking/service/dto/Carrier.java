package net.altitudetech.propass.flight.booking.service.dto;

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
  "marketing",
  "marketingFlightNumber",
  "operating",
  "operatingFlightNumber",
  "equipment"
})
public class Carrier {
  
  @JsonProperty("marketing")
  public String marketing;
  @JsonProperty("marketingFlightNumber")
  public Integer marketingFlightNumber;
  @JsonProperty("operating")
  public String operating;
  @JsonProperty("operatingFlightNumber")
  public Integer operatingFlightNumber;
  @JsonProperty("equipment")
  public Equipment equipment;
}