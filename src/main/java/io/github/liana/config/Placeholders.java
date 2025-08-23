package io.github.liana.config;

public final class Placeholders {

  private Placeholders() {
  }

  public static PlaceholderBuilder builder() {
    return new DefaultPlaceholderBuilder();
  }
}
