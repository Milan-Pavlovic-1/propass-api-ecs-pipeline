package net.altitudetech.propass.voucher.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.dto.BaseDTO;

@Getter
@Setter
@Builder
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
  
}
