package net.altitudetech.propass.flight.service.dto;

import org.springframework.lang.NonNull;
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
public class FlightDTO extends BaseDTO {
  private Long airlineId;
  @NonNull
  private FlightLocationDTO locationFrom;
  @NonNull
  private FlightLocationDTO locationTo;
}