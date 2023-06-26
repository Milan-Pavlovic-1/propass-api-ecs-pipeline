package net.altitudetech.propass.flight.booking.service.config;


import net.altitudetech.propass.flight.booking.service.client.FlightClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import retrofit2.Retrofit;

@Configuration
public class PropassFlightBookingConfig {
  
  @Bean
  public FlightClient flightClient(Retrofit retrofit) {
    return retrofit.create(FlightClient.class);
  }
  
}
