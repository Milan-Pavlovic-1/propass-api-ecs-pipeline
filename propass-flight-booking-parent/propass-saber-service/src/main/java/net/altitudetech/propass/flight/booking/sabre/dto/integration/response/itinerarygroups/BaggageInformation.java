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
        "provisionType",
        "airlineCode",
        "segments",
        "allowance"
})
public class BaggageInformation {
  
  @JsonProperty("provisionType")
  private String provisionType;
  @JsonProperty("airlineCode")
  private String airlineCode;
  @JsonProperty("segments")
  private List<Segment> segments;
  @JsonProperty("allowance")
  private Allowance allowance;
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
  
  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
          "id"
  })
  public static class Segment {
    
    @JsonProperty("id")
    private Integer id;
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
}
