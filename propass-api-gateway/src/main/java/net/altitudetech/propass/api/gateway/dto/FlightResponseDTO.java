package net.altitudetech.propass.api.gateway.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.dto.BaseDTO;

import java.util.LinkedHashMap;
import java.util.Map;

// TODO: for now we return all data from the API, we will decide what we should return later
//  this should be general for both Amadeus, Sabre and possibly others after knowing the required properties to be returned in general

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponseDTO extends BaseDTO {
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
