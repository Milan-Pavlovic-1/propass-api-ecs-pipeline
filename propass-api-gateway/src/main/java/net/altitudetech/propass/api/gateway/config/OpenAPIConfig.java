package net.altitudetech.propass.api.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
  info = @Info(
    title = "Swagger Documentation - Propass",
    description = "The Propass Application",
    version = "1.0.0" // dummy version, it's required when generating openapi.json file
  ),
  servers = {
    @Server(
      description = "HTTPS",
      url = "https://altitude-tech.net/"
    )
  }
)
@SecurityScheme(
  name = "BearerAuth",
  description = "JWT Bearer Authentication",
  scheme = "Bearer",
  type = SecuritySchemeType.HTTP
)
@Configuration
public class OpenAPIConfig {
}
