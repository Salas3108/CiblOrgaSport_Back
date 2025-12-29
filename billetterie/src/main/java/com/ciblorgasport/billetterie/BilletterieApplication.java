package com.ciblorgasport.billetterie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ciblorgasport.billetterie.repository")
public class BilletterieApplication {
  public static void main(String[] args) {
    SpringApplication.run(BilletterieApplication.class, args);
  }
}
