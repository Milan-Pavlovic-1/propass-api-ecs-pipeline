package net.altitudetech.propass.api.gateway.dto;

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
public class VoucherHistoryDTO extends BaseDTO {
  private VoucherDTO voucher;
  private String ticketRef;
  
  public VoucherHistoryDTO(Long id) {
    super(id);
  }
}
