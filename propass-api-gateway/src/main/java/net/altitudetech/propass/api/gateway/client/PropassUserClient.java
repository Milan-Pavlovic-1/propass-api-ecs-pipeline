package net.altitudetech.propass.api.gateway.client;

import net.altitudetech.propass.api.gateway.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PropassUserClient {
  @GET("/users/{id}")
  public Call<UserDTO> getUser(@Path(value = "id") Long id);
}
