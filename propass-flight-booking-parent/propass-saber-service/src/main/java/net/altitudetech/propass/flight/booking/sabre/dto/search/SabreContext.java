package net.altitudetech.propass.flight.booking.sabre.dto.search;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.flight.booking.sabre.config.SabreConfig;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SabreContext<RQ, RS> {
  private RQ request;
  private RS response;
  @Setter(AccessLevel.NONE)
  private SabreConfig config;
  
  public SabreContext(SabreConfig config) {
    this.config = config;
  }
}
