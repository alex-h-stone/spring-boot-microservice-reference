package com.cgi.example.petstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PetStoreApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(PetStoreApplication.class);
    springApplication.run(args);
  }
}
