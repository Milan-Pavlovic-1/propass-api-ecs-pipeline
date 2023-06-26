package net.altitudetech.propass.flight.booking.service.service;

import net.altitudetech.propass.commons.client.RetrofitCaller;
import net.altitudetech.propass.flight.booking.service.client.FlightClient;
import net.altitudetech.propass.flight.booking.service.dto.FlightResponseDTO;
import net.altitudetech.propass.flight.booking.service.dto.RevalidateFlightRequestDTO;
import net.altitudetech.propass.flight.booking.service.dto.SearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.service.dto.SearchFlightResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlightBookingService {
  
  @Autowired
  private RetrofitCaller retrofitCaller;
  @Autowired
  private FlightClient flightClient;
  
  public SearchFlightResponseDTO searchFlights(SearchFlightRequestDTO request) {
    return this.retrofitCaller.sync(this.flightClient.searchFlights(request));
  }
  
  public FlightResponseDTO revalidateFlights(RevalidateFlightRequestDTO request) {
    return this.retrofitCaller.sync(this.flightClient.revalidateFlights(request));
  }
}
