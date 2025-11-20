package io.github.liana.config.core.exception;

public class ConfigLoaderException extends RuntimeException {

  public ConfigLoaderException(String message) {
    super(message);
  }

  public ConfigLoaderException(String message, Throwable cause) {
    super(message, cause);
  }
}
