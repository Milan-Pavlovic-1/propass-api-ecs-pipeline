package net.altitudetech.propass.api.gateway.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.api.gateway.dto.LoginRegisterRequestDTO;
import net.altitudetech.propass.api.gateway.dto.LoginRegisterResponseDTO;
import net.altitudetech.propass.api.gateway.dto.UserDTO;
import net.altitudetech.propass.api.gateway.service.PropassAuthService;
import net.altitudetech.propass.commons.security.annotation.Public;

@RestController
@RequestMapping("/propass/auth")
public class PropassAuthController {
  @Autowired
  private PropassAuthService authService;
  
  @Public
  @PostMapping("/login")
  public LoginRegisterResponseDTO login(@RequestBody LoginRegisterRequestDTO request) {
    return this.authService.login(request);
  }
  
  @Public
  @PostMapping("/register")
  public LoginRegisterResponseDTO register(@RequestBody LoginRegisterRequestDTO request) {
    return this.authService.register(request);
  }
  
  @GetMapping("/me")
  @SecurityRequirement(name = "BearerAuth")
  public UserDTO me() {
    return this.authService.me();
  }
  
}
