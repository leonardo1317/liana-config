package io.github.liana.config;

@FunctionalInterface
public interface KeyNormalizer<K> {
  K normalize(K key);
}
