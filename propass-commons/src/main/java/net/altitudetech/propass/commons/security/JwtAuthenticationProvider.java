package net.altitudetech.propass.commons.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {
  private JwtUtils jwtUtils;

  public JwtAuthenticationProvider(JwtUtils jwtUtils) {
    this.jwtUtils = jwtUtils;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    try {
      String jwtToken = authentication.getPrincipal().toString();
      String username = this.jwtUtils.getUsernameFromJwtToken(jwtToken);
      return new JwtAuthentication(username);
    } catch (Exception e) {
      log.warn("Unable to parse user data from JWT token.");
      throw new BadCredentialsException("Invalid token.");
    }
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(aClass);
  }

}
