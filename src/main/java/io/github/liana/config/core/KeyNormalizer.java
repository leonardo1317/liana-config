package io.github.liana.config.core;

@FunctionalInterface
public interface KeyNormalizer<K> {
  K normalize(K key);
}
