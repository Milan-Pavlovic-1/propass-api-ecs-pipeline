package net.altitudetech.propass.flight.booking.sabre.dto.integration.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.dto.BaseDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.GroupedItineraryResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "groupedItineraryResponse"
})
public class SabreSearchFlightResponseDTO extends BaseDTO {
  @JsonProperty("groupedItineraryResponse")
  private GroupedItineraryResponse groupedItineraryResponse;
}
