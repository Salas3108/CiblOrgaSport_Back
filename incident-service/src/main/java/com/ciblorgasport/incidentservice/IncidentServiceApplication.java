package com.ciblorgasport.incidentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ciblorgasport.incidentservice.repository")
@EnableAsync
public class IncidentServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(IncidentServiceApplication.class, args);
  }
}