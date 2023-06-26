package net.altitudetech.propass.flight.booking.service.controller;

import net.altitudetech.propass.flight.booking.service.dto.FlightResponseDTO;
import net.altitudetech.propass.flight.booking.service.dto.RevalidateFlightRequestDTO;
import net.altitudetech.propass.flight.booking.service.dto.SearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.service.dto.SearchFlightResponseDTO;
import net.altitudetech.propass.flight.booking.service.service.FlightBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flights")
public class FlightBookingController {
  
  @Autowired
  private FlightBookingService flightService;
  
  
  @PostMapping("/search")
  public SearchFlightResponseDTO searchFlights(@RequestBody SearchFlightRequestDTO request) {
    return flightService.searchFlights(request);
  }
  
  @PostMapping("/revalidate")
  public FlightResponseDTO revalidateFlights(@RequestBody RevalidateFlightRequestDTO request) {
    return flightService.revalidateFlights(request);
  }
}
