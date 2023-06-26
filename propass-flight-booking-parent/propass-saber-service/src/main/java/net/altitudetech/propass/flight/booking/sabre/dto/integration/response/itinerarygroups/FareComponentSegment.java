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
        "segment"
})
public class FareComponentSegment {
  
  @JsonProperty("segment")
  private Segment segment;
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
          "bookingCode",
          "dualInventoryCode",
          "cabinCode",
          "mealCode",
          "seatsAvailable",
          "availabilityBreak"
  })
  public static class Segment {
    
    @JsonProperty("bookingCode")
    private String bookingCode;
    @JsonProperty("dualInventoryCode")
    private String dualInventoryCode;
    @JsonProperty("cabinCode")
    private String cabinCode;
    @JsonProperty("mealCode")
    private String mealCode;
    @JsonProperty("seatsAvailable")
    private Integer seatsAvailable;
    @JsonProperty("availabilityBreak")
    private Boolean availabilityBreak;
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
