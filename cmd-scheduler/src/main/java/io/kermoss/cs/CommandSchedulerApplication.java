package io.kermoss.cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
@ComponentScan(value = "io.kermoss.cs")
@EntityScan(value = "io.kermoss.cs.domain")
@EnableJpaRepositories(value = "io.kermoss.cs.domain.repository")
public class CommandSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommandSchedulerApplication.class, args);
    }
}
