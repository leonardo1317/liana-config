package io.github.liana.config;

import java.util.Set;

/**
 * Generic strategy contract for registration.
 *
 * @param <K> the type of the strategy key (String, Set<String>, Enum, etc.)
 */
public interface Strategy<K> {

  /**
   * Unique key(s) that identify this strategy.
   */
  Set<K> getKeys();
}
