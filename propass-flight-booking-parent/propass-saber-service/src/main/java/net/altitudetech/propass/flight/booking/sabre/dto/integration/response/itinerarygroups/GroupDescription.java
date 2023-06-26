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
        "legDescriptions"
})
public class GroupDescription {
  
  @JsonProperty("legDescriptions")
  private List<LegDescription> legDescriptions;
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
