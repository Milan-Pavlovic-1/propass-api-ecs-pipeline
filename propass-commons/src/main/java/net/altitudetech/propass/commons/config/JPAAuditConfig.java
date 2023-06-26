package net.altitudetech.propass.commons.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableJpaAuditing
public class JPAAuditConfig {
  @Bean
  public AuditorAware<String> auditorProvider() {
    return new AuditorAware<String>() {
      @Override
      public Optional<String> getCurrentAuditor() {
        try {
          if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return Optional.ofNullable(username);
          }
        } catch (ClassCastException ignored) {
        }

        return Optional.empty();
      }
    };
  }
}
