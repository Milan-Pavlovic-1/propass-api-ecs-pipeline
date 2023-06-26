package net.altitudetech.propass.flight.booking.sabre.client;

import net.altitudetech.propass.flight.booking.sabre.dto.integration.search.SabreSearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.search.SabreSearchFlightResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SabreFlightClient {
  
  @POST("/v4/offers/shop")
  Call<SabreSearchFlightResponseDTO> searchFlights(@Body SabreSearchFlightRequestDTO request);
  
  @POST("/v4/shop/flights/revalidate")
  Call<SabreSearchFlightResponseDTO> revalidateFlights(@Body SabreSearchFlightRequestDTO request);
}
