package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;

public record ConfigResource(String resourceName, InputStream inputStream) {

  public ConfigResource {
    requireNonNull(resourceName, "resourceName must not be null");
    requireNonNull(inputStream, "inputStream must not be null");
  }
}
