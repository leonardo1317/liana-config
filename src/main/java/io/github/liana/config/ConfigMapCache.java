package io.github.liana.config;

import static java.util.Objects.requireNonNullElseGet;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

final class ConfigMapCache<K, V> {

  private final AtomicReference<Map<K, V>> cache = new AtomicReference<>();

  public Map<K, V> get(Supplier<Map<K, V>> loader) {
    return cache.updateAndGet(current -> requireNonNullElseGet(current,
        () -> Collections.unmodifiableMap(new LinkedHashMap<>(loader.get()))));
  }
}