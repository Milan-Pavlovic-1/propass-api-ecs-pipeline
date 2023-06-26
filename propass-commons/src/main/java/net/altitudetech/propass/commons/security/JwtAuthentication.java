package net.altitudetech.propass.commons.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthentication implements Authentication {
  private static final long serialVersionUID = -8920664629550037147L;
  // TODO change this from simple username to a POJO
  private final String username;
  private final Collection<GrantedAuthority> authorities;

  public JwtAuthentication(String username, Collection<GrantedAuthority> authorities) {
    this.username = username;
    this.authorities = authorities;
  }

  public JwtAuthentication(String username) {
    this(username, null);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getDetails() {
    return this.username;
  }

  @Override
  public Object getPrincipal() {
    return this.username;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public void setAuthenticated(boolean b) throws IllegalArgumentException {

  }

  @Override
  public String getName() {
    return this.username;
  }
}
