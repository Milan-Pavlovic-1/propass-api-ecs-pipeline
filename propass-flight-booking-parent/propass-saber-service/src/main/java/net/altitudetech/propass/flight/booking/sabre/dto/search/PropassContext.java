package net.altitudetech.propass.flight.booking.sabre.dto.search;

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
public class PropassContext<RQ, RS> {
  private RQ request;
  private RS response;
}
