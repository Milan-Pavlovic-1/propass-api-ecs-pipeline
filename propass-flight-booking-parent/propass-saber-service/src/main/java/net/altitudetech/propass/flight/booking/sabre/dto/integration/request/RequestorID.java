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
        "Type",
        "ID",
        "CompanyName"
})
public class RequestorID {
  
  @JsonProperty("Type")
  private String type;
  @JsonProperty("ID")
  private String id;
  @JsonProperty("CompanyName")
  private CompanyName companyName;
}
