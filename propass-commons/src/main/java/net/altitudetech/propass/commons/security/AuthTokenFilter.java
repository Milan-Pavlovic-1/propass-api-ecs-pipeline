package net.altitudetech.propass.commons.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private static final int BEARER_PREFIX_LENGTH = "Bearer".length();

  public AuthTokenFilter(JwtUtils jwtUtils) {
    super();
    this.jwtUtils = jwtUtils;
  }

  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String jwt = request.getHeader("Authorization");
      if (jwt != null) {
        jwt = jwt.substring(BEARER_PREFIX_LENGTH + 1);
        if (jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUsernameFromJwtToken(jwt);
          JwtAuthentication authentication = new JwtAuthentication(username);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e);
    }
    
    filterChain.doFilter(request, response);
  }
  
}
