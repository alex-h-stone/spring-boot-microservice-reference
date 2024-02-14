package com.cgi.example.petstore.integration.config;

import com.cgi.example.petstore.integration.utils.WiremockServerForIntegrationTests;
import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

@Configuration
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        EmbeddedMongoAutoConfiguration.class})
public class IntegrationTestConfig {

    @Autowired
    public void configureForIntegrationTests(ConfigurableEnvironment environment) {
        IntegrationTestPropertySource propertySource = new IntegrationTestPropertySource();
        environment.getPropertySources().addFirst(propertySource);
    }

    private static final class IntegrationTestPropertySource extends MapPropertySource {
        private static final Map<String, Object> PROPERTIES =
                Map.of("thirdparty.apis.vaccinations.baseUrl", "http://localhost:" + WiremockServerForIntegrationTests.getPort());

        public IntegrationTestPropertySource() {
            super(IntegrationTestPropertySource.class.getSimpleName(),
                    PROPERTIES);
        }
    }
}
