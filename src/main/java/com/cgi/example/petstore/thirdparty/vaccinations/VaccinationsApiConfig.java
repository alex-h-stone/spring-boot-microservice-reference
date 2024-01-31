package com.cgi.example.petstore.thirdparty.vaccinations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "thirdparty.apis.vaccinations")
public class VaccinationsApiConfig {

    private String path;
    private String scheme;
    private String host;
    private int port;
}
