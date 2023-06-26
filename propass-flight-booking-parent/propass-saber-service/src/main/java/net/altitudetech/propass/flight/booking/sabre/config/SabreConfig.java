package net.altitudetech.propass.flight.booking.sabre.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.Airline;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.Pos;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.TravelPreferences;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SabreConfig {
  private Pos pos;
  private TravelPreferences travelPreferences;
  private String version;
  private Airline airline;
  private boolean directFlightsOnly;
  private int dateWindowDays;
}
