package net.altitudetech.propass.flight.booking.sabre.dto.integration.response.itinerarygroups;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "totalFare",
        "totalTaxAmount",
        "currency",
        "baseFareAmount",
        "baseFareCurrency",
        "equivalentAmount",
        "equivalentCurrency",
        "constructionAmount",
        "constructionCurrency"
})
public class PassengerTotalFare {
  
  @JsonProperty("totalFare")
  private Double totalFare;
  @JsonProperty("totalTaxAmount")
  private Double totalTaxAmount;
  @JsonProperty("currency")
  private String currency;
  @JsonProperty("baseFareAmount")
  private Double baseFareAmount;
  @JsonProperty("baseFareCurrency")
  private String baseFareCurrency;
  @JsonProperty("equivalentAmount")
  private Double equivalentAmount;
  @JsonProperty("equivalentCurrency")
  private String equivalentCurrency;
  @JsonProperty("constructionAmount")
  private Double constructionAmount;
  @JsonProperty("constructionCurrency")
  private String constructionCurrency;
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
