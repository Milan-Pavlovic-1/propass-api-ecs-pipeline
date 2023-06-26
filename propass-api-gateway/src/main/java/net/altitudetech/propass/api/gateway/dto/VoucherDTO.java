package net.altitudetech.propass.api.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO extends BaseDTO {
  private Integer totalAmount;
  private Integer usedAmount;
  private Long flightId;
  private VoucherTypeDTO voucherType;
  
  public VoucherDTO(Long id) {
    super(id);
  }

  @Builder
  public VoucherDTO(Long id, Integer totalAmount, Integer usedAmount, Long flightId,
      VoucherTypeDTO voucherType) {
    super(id);
    this.totalAmount = totalAmount;
    this.usedAmount = usedAmount;
    this.flightId = flightId;
    this.voucherType = voucherType;
  }
  
}
