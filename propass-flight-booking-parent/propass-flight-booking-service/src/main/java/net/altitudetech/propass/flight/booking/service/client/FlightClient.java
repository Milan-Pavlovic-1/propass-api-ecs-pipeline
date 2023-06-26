package net.altitudetech.propass.flight.booking.service.client;

import net.altitudetech.propass.flight.booking.service.dto.FlightResponseDTO;
import net.altitudetech.propass.flight.booking.service.dto.RevalidateFlightRequestDTO;
import net.altitudetech.propass.flight.booking.service.dto.SearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.service.dto.SearchFlightResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FlightClient {
  
  
  @POST("/sabre/search")
  Call<SearchFlightResponseDTO> searchFlights(@Body SearchFlightRequestDTO request);
  
  @POST("/sabre/revalidate")
  Call<FlightResponseDTO> revalidateFlights(@Body RevalidateFlightRequestDTO request);
}
