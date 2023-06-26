package net.altitudetech.propass.api.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.altitudetech.propass.api.gateway.client.PropassUserClient;
import net.altitudetech.propass.api.gateway.dto.UserDTO;
import net.altitudetech.propass.commons.client.RetrofitCaller;

@Service
public class PropassUserService {
  @Autowired
  private RetrofitCaller retrofitCaller;
  @Autowired
  private PropassUserClient propassUserClient;
  
  public UserDTO findOne(Long id) {
    return this.retrofitCaller.sync(propassUserClient.getUser(id));
  }

}
