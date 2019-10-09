package com.cyssxt.tomato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.cyssxt")
@EnableJpaRepositories(basePackages = "com.cyssxt",repositoryImplementationPostfix="Repository")
@EntityScan("com.cyssxt")
@EnableScheduling
public class TomatoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TomatoApplication.class, args);
    }

}

