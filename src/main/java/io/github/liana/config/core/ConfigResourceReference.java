package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

public record ConfigResourceReference(String provider, String resourceName) {

  public ConfigResourceReference {
    requireNonNull(provider, "provider must not be null");
    requireNonNull(resourceName, "resourceName must not be null");
  }
}
