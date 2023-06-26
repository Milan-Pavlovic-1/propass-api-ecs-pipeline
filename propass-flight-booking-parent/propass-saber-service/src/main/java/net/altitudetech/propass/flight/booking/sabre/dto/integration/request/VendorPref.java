package net.altitudetech.propass.flight.booking.sabre.dto.integration.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorPref {
  @JsonProperty("Type")
  private Type type;
  @JsonProperty("Code")
  private String code;
  @JsonProperty("PreferLevel")
  private PreferenceLevel preferLevel;
  
  public enum Type {
    MARKETING("Marketing"), OPERATING("Operating");
    private String value;
    
    Type(String value) {
      this.value = value;
    }
    
    @JsonValue
    public String value() {
      return this.value;
    }
  }
}
