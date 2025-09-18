package io.github.liana.config.exception;

public class ConfigProviderException extends RuntimeException {

  public ConfigProviderException(String message) {
    super(message);
  }
  public ConfigProviderException(String message, Throwable cause) {
    super(message, cause);
  }
}
