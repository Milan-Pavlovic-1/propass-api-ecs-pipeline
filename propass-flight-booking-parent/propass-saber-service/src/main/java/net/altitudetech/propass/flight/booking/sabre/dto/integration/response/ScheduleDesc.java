package net.altitudetech.propass.flight.booking.sabre.dto.integration.response;

import java.util.LinkedHashMap;
import java.util.Map;
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


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "frequency",
  "stopCount",
  "eTicketable",
  "totalMilesFlown",
  "elapsedTime",
  "departure",
  "arrival",
  "carrier"
})
public class ScheduleDesc {
  
  @JsonProperty("id")
  public Integer id;
  @JsonProperty("frequency")
  public String frequency;
  @JsonProperty("stopCount")
  public Integer stopCount;
  @JsonProperty("eTicketable")
  public Boolean eTicketable;
  @JsonProperty("totalMilesFlown")
  public Integer totalMilesFlown;
  @JsonProperty("elapsedTime")
  public Integer elapsedTime;
  @JsonProperty("departure")
  public Departure departure;
  @JsonProperty("arrival")
  public Arrival arrival;
  @JsonProperty("carrier")
  public Carrier carrier;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
  
}