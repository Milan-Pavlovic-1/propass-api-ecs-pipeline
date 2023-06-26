package net.altitudetech.propass.api.gateway.client;

import net.altitudetech.propass.api.gateway.dto.LoginRegisterRequestDTO;
import net.altitudetech.propass.api.gateway.dto.LoginRegisterResponseDTO;
import net.altitudetech.propass.api.gateway.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PropassAuthClient {
  @POST("/auth/login")
  public Call<LoginRegisterResponseDTO> login(@Body LoginRegisterRequestDTO request);
  
  @POST("/auth/register")
  public Call<LoginRegisterResponseDTO> register(@Body LoginRegisterRequestDTO request);
  
  @GET("/auth/me")
  public Call<UserDTO> me();
}
