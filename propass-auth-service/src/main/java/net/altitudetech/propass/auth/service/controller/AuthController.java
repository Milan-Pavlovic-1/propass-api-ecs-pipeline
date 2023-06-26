package net.altitudetech.propass.auth.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.auth.service.dto.LoginRegisterRequestDTO;
import net.altitudetech.propass.auth.service.dto.LoginRegisterResponseDTO;
import net.altitudetech.propass.auth.service.dto.UserDTO;
import net.altitudetech.propass.auth.service.service.PropassAuthUserService;
import net.altitudetech.propass.commons.security.annotation.Public;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  private PropassAuthUserService userService;

  @Public
  @PostMapping("/register")
  public LoginRegisterResponseDTO register(@RequestBody LoginRegisterRequestDTO request) {
    // TODO decide how to handle airlines
    return this.userService.register(request, 0);
  }

  @Public
  @PostMapping("/login")
  public LoginRegisterResponseDTO login(@RequestBody LoginRegisterRequestDTO request) {
    // TODO decide how to handle airlines
    return this.userService.login(request, 0);
  }
  
  @GetMapping("/me")
  public UserDTO me() {
    // TODO decide how to handle airlines
    return this.userService.me(0);
  }

}
