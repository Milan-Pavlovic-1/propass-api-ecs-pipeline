package net.altitudetech.propass.api.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.altitudetech.propass.api.gateway.client.PropassAuthClient;
import net.altitudetech.propass.api.gateway.dto.LoginRegisterRequestDTO;
import net.altitudetech.propass.api.gateway.dto.LoginRegisterResponseDTO;
import net.altitudetech.propass.api.gateway.dto.UserDTO;
import net.altitudetech.propass.commons.client.RetrofitCaller;

@Service
public class PropassAuthService {
  @Autowired
  private RetrofitCaller retrofitCaller;
  @Autowired
  private PropassAuthClient propassAuthClient;
  
  public LoginRegisterResponseDTO login(LoginRegisterRequestDTO request) {
    return this.retrofitCaller.sync(propassAuthClient.login(request));
  }
  
  public LoginRegisterResponseDTO register(LoginRegisterRequestDTO request) {
    return this.retrofitCaller.sync(propassAuthClient.register(request));
  }
  
  public UserDTO me() {
    return this.retrofitCaller.sync(propassAuthClient.me());
  }

}
