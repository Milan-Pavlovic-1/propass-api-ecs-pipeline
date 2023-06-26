package net.altitudetech.propass.voucher.service.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import net.altitudetech.propass.commons.config.JPAAuditConfig;

@SpringBootApplication
@Import(JPAAuditConfig.class)
@EntityScan(basePackages = { "net.altitudetech.propass" })
@EnableJpaRepositories(basePackages ={ "net.altitudetech.propass" })
@ComponentScan(basePackages = { "net.altitudetech.propass" })
public class PropassVoucherServiceTestConfig {

}
