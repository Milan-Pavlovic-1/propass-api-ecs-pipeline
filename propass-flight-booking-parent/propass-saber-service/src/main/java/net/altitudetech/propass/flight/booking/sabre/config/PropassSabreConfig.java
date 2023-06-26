package net.altitudetech.propass.flight.booking.sabre.config;

import net.altitudetech.propass.flight.booking.sabre.client.SabreFlightClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;


@Configuration
public class PropassSabreConfig {
  
  @Bean
  public SabreFlightClient sabreFlightClient(@Qualifier("sabre-retrofit") Retrofit retrofit) {
    return retrofit.create(SabreFlightClient.class);
  }
}
