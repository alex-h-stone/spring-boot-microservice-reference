package com.cgi.example.petstore.embedded;

import static com.cgi.example.petstore.utils.ProcessManagement.waitUntil;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import de.flapdoodle.embed.mongo.commands.ServerAddress;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.ImmutableMongod;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmbeddedMongoDB implements ManageableService {

  private final DynamicApplicationPropertiesRepository propertiesRepository =
      new DynamicApplicationPropertiesRepository();
  private TransitionWalker.ReachedState<RunningMongodProcess> runningMongoDB;

  public static void main(String[] args) {
    EmbeddedMongoDB mongoDB = new EmbeddedMongoDB();
    mongoDB.start();
  }

  @Override
  public void start() {
    if (isRunning()) {
      log.debug("Cannot start Embedded MongoDB as it is already running");
      return;
    }

    ImmutableMongod mongoDB =
        Mongod.builder().net(Start.to(Net.class).initializedWith(Net.defaults())).build();

    log.info("Starting Embedded MongoDB");
    executeAsDetachedThread(() -> runningMongoDB = mongoDB.start(Version.V4_4_18));

    waitUntil(this::isRunning);

    ServerAddress serverAddress = runningMongoDB.current().getServerAddress();
    String host = serverAddress.getHost();
    int port = serverAddress.getPort();
    log.info("Started Embedded MongoDB on {}:{}", host, port);

    propertiesRepository.setMongoDBPort(getClass(), port);
    blockAndWait();
  }

  private static void blockAndWait() {
    try {
      System.in.read();
    } catch (IOException e) {
      String message = "Embedded MongoDB process is exiting: [%s]".formatted(e.getMessage());
      log.info(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private void executeAsDetachedThread(Runnable runnable) {
    Thread detachedThread = new Thread(runnable);
    detachedThread.start();
  }

  @Override
  public void stop() {
    if (!isRunning()) {
      log.debug("Cannot stop Embedded MongoDB as it has already stopped");
      return;
    }

    log.info("Shutting down Embedded MongoDB");
    runningMongoDB.close();
    waitUntil(() -> !isRunning());
    log.info("Embedded MongoDB has shut down");
  }

  @Override
  public boolean isRunning() {
    return Objects.nonNull(runningMongoDB)
        && Objects.nonNull(runningMongoDB.current())
        && runningMongoDB.current().isAlive();
  }
}
