package net.altitudetech.propass.voucher.service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class VoucherTypeDTO extends BaseDTO {
  private String name;
  private Long airlineId;
  private JsonNode priceFormula;
  private JsonNode restrictionFormula;
  private Double price;
  
  @Builder
  public VoucherTypeDTO(Long id, String name, Long airlineId, JsonNode priceFormula,
      JsonNode restrictionFormula, Double price) {
    super(id);
    this.name = name;
    this.airlineId = airlineId;
    this.priceFormula = priceFormula;
    this.restrictionFormula = restrictionFormula;
    this.price = price;
  }
  
}
