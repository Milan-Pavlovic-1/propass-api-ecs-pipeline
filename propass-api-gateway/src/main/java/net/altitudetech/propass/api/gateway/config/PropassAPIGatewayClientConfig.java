package net.altitudetech.propass.api.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import net.altitudetech.propass.api.gateway.client.PropassAuthClient;
import net.altitudetech.propass.api.gateway.client.PropassUserClient;
import net.altitudetech.propass.api.gateway.client.PropassVoucherClient;
import net.altitudetech.propass.api.gateway.client.PropassFlightClient;
import retrofit2.Retrofit;

@Configuration
public class PropassAPIGatewayClientConfig {
  @Bean
  public PropassUserClient userClient(Retrofit retrofit) {
    return retrofit.create(PropassUserClient.class);
  }
  
  @Bean
  public PropassAuthClient authClient(Retrofit retrofit) {
    return retrofit.create(PropassAuthClient.class);
  }
  
  @Bean
  public PropassVoucherClient voucherClient(Retrofit retrofit) {
    return retrofit.create(PropassVoucherClient.class);
  }
  
  @Bean
  public PropassFlightClient propassFlightClient(Retrofit retrofit) {
    return retrofit.create(PropassFlightClient.class);
  }
}
