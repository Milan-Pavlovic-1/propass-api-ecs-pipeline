package net.altitudetech.propass.flight.service.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Embeddable
@NoArgsConstructor
public class FlightLocation {
  private String name;
  private String code;
  private String type;
}
