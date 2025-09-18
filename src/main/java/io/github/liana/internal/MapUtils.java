package io.github.liana.internal;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for creating and manipulating {@link Map} instances.
 *
 * <p>This class provides convenience methods for constructing maps in a concise and type-safe way.
 * It is a non-instantiable utility class.
 */
public final class MapUtils {

  private MapUtils() {
  }

  /**
   * Converts an array of key-value elements into an unmodifiable {@link Map}.
   *
   * <p>The array must contain an even number of elements, where elements at even indexes are
   * treated as keys and the subsequent elements as their corresponding values.
   *
   * <p>For example:
   *
   * <pre>{@code
   * Map<String, String> map = MapUtils.of("key1", "value1", "key2", "value2");
   * }</pre>
   *
   * <p>This method is generic and can be used with any type {@code T}, but it assumes that all
   * keys and values share the same type.
   *
   * @param <T>     the type of both keys and values
   * @param entries an array containing alternating keys and values; must not be {@code null} and
   *                must have an even length
   * @return an unmodifiable {@link HashMap} containing the key-value pairs
   * @throws NullPointerException     if {@code entries} is {@code null}
   * @throws IllegalArgumentException if {@code entries} has an odd number of elements, meaning
   *                                  there is a key without a corresponding value
   */
  @SafeVarargs
  public static <T> Map<T, T> of(T... entries) {
    requireNonNull(entries, "entries must not be null");
    final int numEntries = entries.length;

    if (numEntries % 2 != 0) {
      throw new IllegalArgumentException("Missing value for key: " + entries[numEntries - 1]);
    }

    var map = new HashMap<T, T>(numEntries / 2);
    for (int i = 0; i < numEntries; i += 2) {
      T key = entries[i];
      T value = entries[i + 1];
      map.put(key, value);
    }

    return Collections.unmodifiableMap(map);
  }
}
