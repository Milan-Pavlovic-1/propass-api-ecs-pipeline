package net.altitudetech.propass.flight.booking.sabre.dto.integration.request;

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
  "Code",
  "Quantity"
})
public class PassengerTypeQuantity {
  
  @JsonProperty("Code")
  private String code;
  @JsonProperty("Quantity")
  private Integer quantity;
  
  public static PassengerTypeQuantity of(String code, Integer quantity) {
    return PassengerTypeQuantity.builder().code(code).quantity(quantity).build();
  }
}
