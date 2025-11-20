package io.github.liana.config.api;

public interface PlaceholderBuilder {

  PlaceholderBuilder prefix(String prefix);

  PlaceholderBuilder suffix(String suffix);

  PlaceholderBuilder delimiter(String delimiter);

  PlaceholderBuilder escapeChar(char escapeChar);

  Placeholder build();
}
