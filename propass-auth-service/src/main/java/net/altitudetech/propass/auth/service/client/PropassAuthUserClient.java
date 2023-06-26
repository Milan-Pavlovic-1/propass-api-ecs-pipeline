package net.altitudetech.propass.auth.service.client;

import org.springframework.data.domain.Page;
import net.altitudetech.propass.auth.service.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PropassAuthUserClient {
  @GET("/users")
  public Call<Page<UserDTO>> getUsers(@Query("email") String email, @Query("company") int company);

  @POST("/users")
  public Call<UserDTO> createUser(@Body UserDTO user);
}
