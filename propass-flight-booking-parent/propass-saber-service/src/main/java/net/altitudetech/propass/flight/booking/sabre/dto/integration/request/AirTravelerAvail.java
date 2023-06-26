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
  "PassengerTypeQuantity"
})
public class AirTravelerAvail {
  
  @JsonProperty("PassengerTypeQuantity")
  private List<PassengerTypeQuantity> passengerTypeQuantity;
  
  public static AirTravelerAvail of(List<PassengerTypeQuantity> passengerTypeQuantity) {
    return builder().passengerTypeQuantity(passengerTypeQuantity).build();
  }
  
}

