package net.altitudetech.propass.flight.booking.sabre.dto.integration.response.itinerarygroups;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "validatingCarrierCode",
        "vita",
        "eTicketable",
        "lastTicketDate",
        "lastTicketTime",
        "governingCarriers",
        "passengerInfoList",
        "totalFare",
        "validatingCarriers"
})
public class Fare {
  
  @JsonProperty("validatingCarrierCode")
  private String validatingCarrierCode;
  @JsonProperty("vita")
  private Boolean vita;
  @JsonProperty("eTicketable")
  private Boolean eTicketable;
  @JsonProperty("lastTicketDate")
  private String lastTicketDate;
  @JsonProperty("lastTicketTime")
  private String lastTicketTime;
  @JsonProperty("governingCarriers")
  private String governingCarriers;
  @JsonProperty("passengerInfoList")
  private List<PassengerInfoList> passengerInfoList;
  @JsonProperty("totalFare")
  private TotalFare totalFare;
  @JsonProperty("validatingCarriers")
  private List<ValidatingCarrier> validatingCarriers;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new LinkedHashMap<>();
  
  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }
  
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
