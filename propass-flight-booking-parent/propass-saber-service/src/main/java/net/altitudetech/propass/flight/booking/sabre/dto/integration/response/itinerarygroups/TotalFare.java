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
        "totalPrice",
        "totalTaxAmount",
        "currency",
        "baseFareAmount",
        "baseFareCurrency",
        "constructionAmount",
        "constructionCurrency",
        "equivalentAmount",
        "equivalentCurrency"
})
public class TotalFare {
  
  @JsonProperty("totalPrice")
  private Double totalPrice;
  @JsonProperty("totalTaxAmount")
  private Double totalTaxAmount;
  @JsonProperty("currency")
  private String currency;
  @JsonProperty("baseFareAmount")
  private Double baseFareAmount;
  @JsonProperty("baseFareCurrency")
  private String baseFareCurrency;
  @JsonProperty("constructionAmount")
  private Double constructionAmount;
  @JsonProperty("constructionCurrency")
  private String constructionCurrency;
  @JsonProperty("equivalentAmount")
  private Double equivalentAmount;
  @JsonProperty("equivalentCurrency")
  private String equivalentCurrency;
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
