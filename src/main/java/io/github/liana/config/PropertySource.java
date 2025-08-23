package io.github.liana.config;

@FunctionalInterface
public interface PropertySource {

  String get(String key);
}
