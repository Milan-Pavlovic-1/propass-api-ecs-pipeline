package net.altitudetech.propass.flight.booking.sabre.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


@Configuration()
public class SabreRetrofitConfig {
  @Value("${propass.service.booking.flight.sabre.baseUrl:https://api-crt.cert.havail.sabre.com}")
  private String baseUrl;
  @Value("${propass.service.booking.flight.sabre.token:}")
  private String sabreToken;
  
  @Bean(name = {"sabre-httpclient"})
  public OkHttpClient httpClient() {
    return new OkHttpClient.Builder().addInterceptor(chain -> {
      // TODO: this is for the PoC, it most likely will be changed later to handle authentication flow of sabre
      Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer " + sabreToken).build();
      return chain.proceed(request);
    }).build();
  }
  
  @Bean(name = {"sabre-retrofit"})
  public Retrofit retrofit(@Qualifier("sabre-httpclient") OkHttpClient httpClient, ObjectMapper objectMapper) {
    return new Retrofit.Builder()
            .baseUrl(this.baseUrl)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(httpClient)
            .build();
  }
}
