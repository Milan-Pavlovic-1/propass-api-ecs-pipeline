package net.altitudetech.propass.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.altitudetech.propass.commons.client.AuthorizationHeaderOkHttpInterceptor;
import net.altitudetech.propass.commons.client.DecodingOkHttpInterceptor;
import net.altitudetech.propass.commons.converter.retrofit.ObjectToPathMapMapper;
import net.altitudetech.propass.commons.converter.retrofit.PageableConverterFactory;
import net.altitudetech.propass.commons.util.json.PageModule;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class RetrofitConfig {
  @Value("${propass.service.user.baseUrl:http://localhost:8080/}")
  private String baseUrl;
  
  @Bean
  public OkHttpClient httpClient() {
    return new OkHttpClient.Builder()
        .addInterceptor(new AuthorizationHeaderOkHttpInterceptor())
        .addInterceptor(new DecodingOkHttpInterceptor())
        .build();
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new PageModule());
    return objectMapper;
  }
  
  @Bean
  public ObjectToPathMapMapper objectToPathMapMapper() {
    return new ObjectToPathMapMapper();
  }
  
  @Bean
  public Retrofit retrofit(OkHttpClient httpClient, ObjectMapper objectMapper) {
    return new Retrofit.Builder()
        .baseUrl(this.baseUrl)
        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
        .addConverterFactory(new PageableConverterFactory())
        .client(httpClient)
        .build();
  }
}
