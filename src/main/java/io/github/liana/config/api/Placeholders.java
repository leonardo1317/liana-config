package io.github.liana.config.api;

import io.github.liana.config.core.DefaultPlaceholderBuilder;

/**
 * Factory for creating {@link PlaceholderBuilder} instances.
 *
 * <p>This class provides a convenient static entry point for constructing
 * placeholder templates using a {@link PlaceholderBuilder}.
 *
 * <p>Example usage:
 * <pre>{@code
 * PlaceholderBuilder builder = Placeholders.builder();
 * String result = builder.add("name", "Alice")
 *                       .add("city", "Paris")
 *                       .build("Hello {name} from {city}!");
 * }</pre>
 *
 * <p>This class is non-instantiable.
 */
public final class Placeholders {

  private Placeholders() {
  }

  public static PlaceholderBuilder builder() {
    return new DefaultPlaceholderBuilder();
  }
}
