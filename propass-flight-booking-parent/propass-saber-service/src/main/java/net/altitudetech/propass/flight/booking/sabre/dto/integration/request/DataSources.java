package net.altitudetech.propass.flight.booking.sabre.dto.integration.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
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
  "NDC",
  "ATPCO",
  "LCC"
})
public class DataSources {
  
  @JsonProperty("NDC")
  private Status ndc;
  @JsonProperty("ATPCO")
  private Status atpco;
  @JsonProperty("LCC")
  private Status lcc;
  
  public enum Status {
    ENABLE("Enable"), DISABLE("Disable");
    private String value;
    
    Status(String value) {
      this.value = value;
    }
    
    @JsonValue
    public String value() {
      return value;
    }
  }
  
}
