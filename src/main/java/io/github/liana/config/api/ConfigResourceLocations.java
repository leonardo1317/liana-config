package io.github.liana.config.api;

import io.github.liana.config.core.DefaultConfigResourceLocationBuilder;

public final class ConfigResourceLocations {

  private ConfigResourceLocations() {
  }

  public static ConfigResourceLocationBuilder builder() {
    return new DefaultConfigResourceLocationBuilder();
  }
}
