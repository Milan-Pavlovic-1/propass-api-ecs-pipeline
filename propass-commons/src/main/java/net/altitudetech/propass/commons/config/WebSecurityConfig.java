package net.altitudetech.propass.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import net.altitudetech.propass.commons.security.AuthTokenFilter;
import net.altitudetech.propass.commons.security.JwtAuthenticationProvider;
import net.altitudetech.propass.commons.security.JwtUtils;
import net.altitudetech.propass.commons.security.matcher.PublicRequestMatcher;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
  @Value("${propass.security.jwt.secret:}")
  private String secret;

  @Value("${propass.security.jwt.expirationms:60000}")
  private long tokenExpiration;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter(jwtUtils());
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    return new JwtAuthenticationProvider(jwtUtils());
  }

  @Bean
  public JwtUtils jwtUtils() {
    return new JwtUtils(this.secret, this.tokenExpiration);
  }
  
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      AuthenticationProvider authenticationProvider, AuthTokenFilter authenticationJwtTokenFilter, RequestMappingHandlerMapping mapping)
      throws Exception {
    http
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      .cors().and().csrf().disable().authorizeHttpRequests()
      .requestMatchers(new PublicRequestMatcher(mapping)).permitAll()
      .requestMatchers("/error").permitAll() // added this to allow actual status to be returned instead of 403
      .requestMatchers("/swagger-ui/*").permitAll()
      .requestMatchers("/v3/api-docs/**").permitAll()
      .anyRequest().authenticated();

    http.authenticationProvider(authenticationProvider);

    http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

}
