package net.altitudetech.propass.flight.booking.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "code",
  "typeForFirstLeg",
  "typeForLastLeg"
})
public class Equipment {
  
  @JsonProperty("code")
  public String code;
  @JsonProperty("typeForFirstLeg")
  public String typeForFirstLeg;
  @JsonProperty("typeForLastLeg")
  public String typeForLastLeg;
  
}