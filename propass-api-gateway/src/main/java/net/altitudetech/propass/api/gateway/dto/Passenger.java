package net.altitudetech.propass.api.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Passenger {
  
  @JsonProperty("code")
  private TypeCodes code;
  @JsonProperty("quantity")
  private Integer quantity;
  
  public enum TypeCodes {
    ADT, CHD, INF, INS, UNN
  }
}
