package net.altitudetech.propass.auth.service.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import net.altitudetech.propass.auth.service.client.PropassAuthUserClient;
import net.altitudetech.propass.auth.service.dto.LoginRegisterRequestDTO;
import net.altitudetech.propass.auth.service.dto.LoginRegisterResponseDTO;
import net.altitudetech.propass.auth.service.dto.UserDTO;
import net.altitudetech.propass.commons.client.RetrofitCaller;
import net.altitudetech.propass.commons.exception.ForbiddenException;
import net.altitudetech.propass.commons.exception.NotFoundException;
import net.altitudetech.propass.commons.security.JwtUtils;

@Service
public class PropassAuthUserService {
  @Autowired
  private RetrofitCaller retrofitCaller;
  @Autowired
  private PropassAuthUserClient authUserClient;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private JwtUtils jwtUtils;

  public LoginRegisterResponseDTO login(LoginRegisterRequestDTO request, int company) {
    UserDTO user = getUser(request, company)
        .orElseThrow(() -> new ForbiddenException("Email or password invalid."));
    if (!this.passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new ForbiddenException("Email or password invalid.");
    } else {
      return prepareResponse(user);
    }
  }

  public LoginRegisterResponseDTO register(LoginRegisterRequestDTO request, int company) {
    try {
      if (getUser(request, company).isPresent()) {
        throw new ForbiddenException("Email already exists.");
      }
    } catch (NotFoundException e) {
      // expected
    }

    UserDTO dto = UserDTO.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .password(this.passwordEncoder.encode(request.getPassword()))
        .build();
    
    UserDTO user =
        this.retrofitCaller.sync(this.authUserClient.createUser(dto));

    return prepareResponse(user);
  }

  private LoginRegisterResponseDTO prepareResponse(UserDTO user) {
    return LoginRegisterResponseDTO.builder()
        .token(this.jwtUtils.generateTokenFromUsername(user.getEmail())).build();
  }

  private Optional<UserDTO> getUser(LoginRegisterRequestDTO request, int company) {
    return getUser(request.getEmail(), company);
  }
  
  private Optional<UserDTO> getUser(String email, int company) {
    Page<UserDTO> users =
        this.retrofitCaller.sync(this.authUserClient.getUsers(email, company));

    if (users.getTotalElements() == 0) {
      return Optional.empty();
    }

    return Optional.of(users.getContent().get(0));
  }

  public UserDTO me(int company) {
    String email =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
    UserDTO user = getUser(email, company)
        .orElseThrow(() -> new ForbiddenException("Unkown user."));
    
    return user;
  }

}
