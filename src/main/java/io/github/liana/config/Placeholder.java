package io.github.liana.config;

import java.util.Map;
import java.util.Optional;

public interface Placeholder {

  Optional<String> replaceIfAllResolvable(String template, PropertySource... extraSources);

  default Optional<String> replaceIfAllResolvable(String template,
      Map<String, String> extraValues) {
    return replaceIfAllResolvable(template, PropertySources.fromMap(extraValues));
  }
}
