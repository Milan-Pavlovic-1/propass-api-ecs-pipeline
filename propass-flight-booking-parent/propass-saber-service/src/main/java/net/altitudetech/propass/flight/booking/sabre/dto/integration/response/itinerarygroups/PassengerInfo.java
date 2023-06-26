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
        "passengerType",
        "passengerNumber",
        "nonRefundable",
        "fareComponents",
        "taxes",
        "taxSummaries",
        "currencyConversion",
        "passengerTotalFare",
        "baggageInformation"
})
public class PassengerInfo {
  
  @JsonProperty("passengerType")
  private String passengerType;
  @JsonProperty("passengerNumber")
  private Integer passengerNumber;
  @JsonProperty("nonRefundable")
  private Boolean nonRefundable;
  @JsonProperty("fareComponents")
  private List<FareComponent> fareComponents;
  @JsonProperty("taxes")
  private List<Tax> taxes;
  @JsonProperty("taxSummaries")
  private List<TaxSummary> taxSummaries;
  @JsonProperty("currencyConversion")
  private CurrencyConversion currencyConversion;
  @JsonProperty("passengerTotalFare")
  private PassengerTotalFare passengerTotalFare;
  @JsonProperty("baggageInformation")
  private List<BaggageInformation> baggageInformation;
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
