package io.github.liana.config;

@FunctionalInterface
public interface ThrowingRunnable {

  void run() throws Exception;
}
