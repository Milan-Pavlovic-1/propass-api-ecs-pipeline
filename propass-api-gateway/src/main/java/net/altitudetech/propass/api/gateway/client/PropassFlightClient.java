package net.altitudetech.propass.api.gateway.client;

import java.util.Map;

import net.altitudetech.propass.api.gateway.dto.SearchFlightResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import net.altitudetech.propass.api.gateway.dto.FlightDTO;
import net.altitudetech.propass.api.gateway.dto.FlightResponseDTO;
import net.altitudetech.propass.api.gateway.dto.RevalidateFlightRequestDTO;
import net.altitudetech.propass.api.gateway.dto.SearchFlightRequestDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface PropassFlightClient {
  @POST("/flights/search")
  public Call<SearchFlightResponseDTO> searchFlights(@Body SearchFlightRequestDTO request);

  @POST("/flights/revalidate")
  public Call<FlightResponseDTO> revalidateFlights(@Body RevalidateFlightRequestDTO request);

  @GET("/flights")
  public Call<Page<FlightDTO>> getFlights(@QueryMap Map<String, Object> filter,
      @Query(value = "page", encoded = true) Pageable pageable);

  @GET("/flights/{flightId}")
  public Call<FlightDTO> getFlight(@Path(value = "flightId") Long flightId);

}
