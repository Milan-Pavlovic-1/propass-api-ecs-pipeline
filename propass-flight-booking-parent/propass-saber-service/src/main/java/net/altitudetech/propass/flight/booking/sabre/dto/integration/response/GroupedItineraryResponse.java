package net.altitudetech.propass.flight.booking.sabre.dto.integration.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.itinerarygroups.ItineraryGroup;

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
  "statistics",
  "scheduleDescs",
  "legDescs",
  "itineraryGroups"
})
public class GroupedItineraryResponse {
  
  @JsonProperty("statistics")
  private Statistics statistics;
  @Singular
  @JsonProperty("scheduleDescs")
  public List<ScheduleDesc> scheduleDescs;
  @Singular
  @JsonProperty("legDescs")
  private List<LegDescs> legDescs;
  @JsonProperty("itineraryGroups")
  private List<ItineraryGroup> itineraryGroups;
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
