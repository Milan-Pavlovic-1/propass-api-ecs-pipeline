package net.altitudetech.propass.flight.booking.sabre.dto.integration.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "elapsedTime",
  "schedules"
})
public class LegDescs {
  
  @JsonProperty("id")
  private Integer id;
  @JsonProperty("elapsedTime")
  private Integer elapsedTime;
  
  @Singular
  @JsonProperty("schedules")
  private List<Schedule> schedules;
  
  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
    "ref",
    "departureDateAdjustment",
  })
  public static class Schedule {
    
    @JsonProperty("ref")
    private Integer ref;
    @JsonProperty("departureDateAdjustment")
    private Integer departureDateAdjustment;
  }
}
