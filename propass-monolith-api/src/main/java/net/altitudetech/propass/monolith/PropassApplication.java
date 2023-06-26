package net.altitudetech.propass.monolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import net.altitudetech.propass.commons.config.JPAAuditConfig;

@SpringBootApplication
@EntityScan(basePackages = { "net.altitudetech.propass" })
@EnableJpaRepositories(basePackages ={ "net.altitudetech.propass" })
@ComponentScan(basePackages = { "net.altitudetech.propass" })
@Import({JPAAuditConfig.class})
public class PropassApplication {
  public static void main(String[] args) {
    SpringApplication.run(PropassApplication.class, args);
  }
}
