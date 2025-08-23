package io.github.liana.config;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for creating {@link PropertySource} instances from various sources.
 *
 * <p>This class provides factory methods for creating property sources backed by
 * environment variables, maps, or other {@link PropertySource} instances.
 *
 * <p>Example usage:
 * <pre>{@code
 * PropertySource env = PropertySources.fromEnv();
 * PropertySource map = PropertySources.fromMap(Map.of("key", "value"));
 * PropertySource identity = PropertySources.from(env);
 * }</pre>
 *
 * <p>This class is final and cannot be instantiated.
 */
public final class PropertySources {

  private PropertySources() {
  }

  /**
   * Creates a {@link PropertySource} backed by the system environment variables.
   *
   * <p>The returned source resolves keys using {@link System#getenv(String)}.
   *
   * @return a property source that retrieves values from environment variables
   */
  public static PropertySource fromEnv() {
    return from(System::getenv);
  }

  /**
   * Creates a {@link PropertySource} backed by the given map.
   *
   * <p>The provided map is defensively copied and wrapped in an unmodifiable view.
   * Values are converted to strings using {@link Object#toString()}.
   *
   * @param map the source map, may be {@code null}. If {@code null}, an empty map is used.
   * @return a property source that retrieves values from the given map
   */
  public static PropertySource fromMap(Map<String, ?> map) {
    Map<String, ?> source = Collections.unmodifiableMap(
        new HashMap<>(requireNonNullElse(map, Collections.emptyMap())));
    PropertySource mapSource = key -> source.get(key) != null ? source.get(key).toString() : null;
    return from(mapSource);
  }

  /**
   * Identity function for {@link PropertySource}.
   *
   * <p>This method returns the given source unchanged. It can be used as a common
   * entry point for adapting property sources created by other methods.
   *
   * @param source the property source to return
   * @return the same {@link PropertySource} instance that was provided
   * @throws NullPointerException if {@code source} is {@code null}
   */
  public static PropertySource from(PropertySource source) {
    return requireNonNull(source, "source must not be null");
  }
}
