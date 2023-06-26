package net.altitudetech.propass;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import net.altitudetech.propass.api.gateway.service.BoardingPassService;
import net.altitudetech.propass.api.gateway.service.EmailService;
import net.altitudetech.propass.monolith.PropassApplication;

@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = {PropassApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PropassApplicationIT {

  // TODO remove mocks after refactor of api-gateway
  @MockBean
  private BoardingPassService boardingPassService;

  @MockBean
  private EmailService emailService;

  @Test
  void contextLoads() {}

}
