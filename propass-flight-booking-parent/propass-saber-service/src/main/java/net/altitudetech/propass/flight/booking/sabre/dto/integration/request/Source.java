package net.altitudetech.propass.flight.booking.sabre.dto.integration.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "PseudoCityCode",
        "RequestorID"
})
public class Source {
  
  @JsonProperty("PseudoCityCode")
  private String pseudoCityCode;
  @JsonProperty("RequestorID")
  private RequestorID requestorID;
}
