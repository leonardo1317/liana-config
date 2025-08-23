package io.github.liana.config;

public final class ConfigResourceLocations {

  private ConfigResourceLocations() {
  }

  public static ConfigResourceLocationBuilder builder() {
    return new DefaultConfigResourceLocationBuilder();
  }
}
