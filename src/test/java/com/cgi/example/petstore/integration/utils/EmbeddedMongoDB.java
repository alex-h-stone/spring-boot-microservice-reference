package com.cgi.example.petstore.integration.utils;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.ImmutableStart;
import de.flapdoodle.reverse.transitions.Start;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

@Component
public class EmbeddedMongoDB implements SmartLifecycle {

    private static final int MONGO_DB_PORT;
    private static String mongoDbHost;
    private static TransitionWalker.ReachedState<RunningMongodProcess> runningMongoDB;

    static {
        try (var serverSocket = new ServerSocket(0)) {
            MONGO_DB_PORT = serverSocket.getLocalPort();
        } catch (IOException e) {
            String message = "Unable to establish a Mongo DB port: [%s]".formatted(e.getMessage());
            throw new RuntimeException(message, e);
        }
    }

    @DynamicPropertySource
    static void overrideConfigurationForTesting(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", () -> mongoDbHost);
        registry.add("spring.data.mongodb.port", () -> MONGO_DB_PORT);
        registry.add("de.flapdoodle.mongodb.embedded.version", () -> "4.4.18");
    }

    @Bean
    public MongoTemplate mongoTemplate(@Value("${spring.data.mongodb.database}") String databaseName) {
        start();
        ConnectionString connectionString = new ConnectionString(String.format("mongodb://%s:%d", mongoDbHost, MONGO_DB_PORT));
        MongoClient mongoClient = MongoClients.create(connectionString);

        return new MongoTemplate(mongoClient, databaseName);
    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }

        ImmutableStart<Net> networkConfig = Start.to(Net.class)
                .initializedWith(Net.defaults()
                        .withPort(MONGO_DB_PORT));

        runningMongoDB = Mongod.builder()
                .net(networkConfig)
                .build()
                .start(Version.V4_4_18);
        mongoDbHost = runningMongoDB.current().getServerAddress().getHost();
    }

    @Override
    public void stop() {
        // Do nothing
    }

    @Override
    public boolean isRunning() {
        if (Objects.isNull(runningMongoDB)) {
            return false;
        }
        return runningMongoDB.current().isAlive();
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }
}
