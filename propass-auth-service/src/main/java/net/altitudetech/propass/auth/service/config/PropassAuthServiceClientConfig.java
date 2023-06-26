package net.altitudetech.propass.auth.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import net.altitudetech.propass.auth.service.client.PropassAuthUserClient;
import retrofit2.Retrofit;

@Configuration
public class PropassAuthServiceClientConfig {

  @Bean
  public PropassAuthUserClient authUserClient(Retrofit retrofit) {
    return retrofit.create(PropassAuthUserClient.class);
  }

}
