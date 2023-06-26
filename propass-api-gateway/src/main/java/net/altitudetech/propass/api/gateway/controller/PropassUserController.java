package net.altitudetech.propass.api.gateway.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.api.gateway.dto.UserDTO;
import net.altitudetech.propass.api.gateway.service.PropassUserService;

@RestController
@RequestMapping("/propass/users")
@SecurityRequirement(name = "BearerAuth")
public class PropassUserController {
  @Autowired
  private PropassUserService userService;
  
  @GetMapping("/{id}")
  public UserDTO single(@PathVariable Long id) {
    return this.userService.findOne(id);
  }
  
}
