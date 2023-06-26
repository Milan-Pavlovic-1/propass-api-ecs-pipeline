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
        "id",
        "pricingSource",
        "legs",
        "pricingInformation",
        "diversitySwapper"
})
public class Itinerary {
  
  @JsonProperty("id")
  private Integer id;
  @JsonProperty("pricingSource")
  private String pricingSource;
  @JsonProperty("legs")
  private List<Leg> legs;
  @JsonProperty("pricingInformation")
  private List<PricingInformation> pricingInformation;
  @JsonProperty("diversitySwapper")
  private DiversitySwapper diversitySwapper;
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
