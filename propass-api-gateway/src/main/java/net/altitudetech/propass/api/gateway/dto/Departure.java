package net.altitudetech.propass.api.gateway.dto;

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
  "airport",
  "city",
  "country",
  "time",
  "state"
})
public class Departure {
  
  @JsonProperty("airport")
  public String airport;
  @JsonProperty("city")
  public String city;
  @JsonProperty("country")
  public String country;
  @JsonProperty("time")
  public String time;
  @JsonProperty("state")
  private String state;
  
}