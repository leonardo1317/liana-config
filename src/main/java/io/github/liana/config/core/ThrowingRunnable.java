package io.github.liana.config.core;

@FunctionalInterface
public interface ThrowingRunnable {

  void run() throws Exception;
}
