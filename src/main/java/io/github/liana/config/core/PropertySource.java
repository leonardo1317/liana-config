package io.github.liana.config.core;

@FunctionalInterface
public interface PropertySource {

  String get(String key);
}
