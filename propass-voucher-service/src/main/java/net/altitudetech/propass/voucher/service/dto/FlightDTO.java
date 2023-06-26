package net.altitudetech.propass.voucher.service.dto;

import java.time.LocalDateTime;
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
public class FlightDTO extends BaseDTO {
  private Double price;
  private LocalDateTime departureDate;
  private String flightClass;
  private Integer vouchers;
  
  @Builder
  public FlightDTO(Long id, Double price, LocalDateTime departureDate, String flightClass, Integer vouchers) {
    super(id);
    this.price = price;
    this.departureDate = departureDate;
    this.flightClass = flightClass;
    this.vouchers = vouchers;
  }
}
