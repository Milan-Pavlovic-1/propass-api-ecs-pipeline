package net.altitudetech.propass.api.gateway.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import net.altitudetech.propass.api.gateway.service.BoardingPassService;
import net.altitudetech.propass.api.gateway.service.EmailService;

@SpringBootApplication
@ComponentScan(basePackages = { "net.altitudetech.propass" })
public class PropassAPIGatewayTestConfig {
  // TODO remove mocks after refactor / these are mocked because they should not be part of this
  // service
  @MockBean
  private BoardingPassService boardingPassService;

  @MockBean
  private EmailService emailService;
}
