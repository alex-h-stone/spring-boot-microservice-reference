package com.cgi.example.petstore.embedded;

public interface ManageableService {

  void start();

  void stop();

  boolean isRunning();
}
