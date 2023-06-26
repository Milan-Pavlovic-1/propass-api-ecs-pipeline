package net.altitudetech.propass.flight.booking.sabre.dto.integration.response;

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
  "itineraryCount"
})
public class Statistics {
  
  @JsonProperty("itineraryCount")
  private Integer itineraryCount;
  
  public static Statistics of(int count) {
    return builder().itineraryCount(count).build();
  }
  
}
