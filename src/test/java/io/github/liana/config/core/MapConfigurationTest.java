package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MapConfigurationTest {

  @Test
  @DisplayName("should create instance with nested map")
  void shouldCreateInstanceWithNestedMap() {
    Map<String, Object> data = Map.of("key", "value");
    MapConfiguration config = new MapConfiguration(data);

    Optional<String> result = config.get("key", String.class);

    assertTrue(result.isPresent());
    assertEquals("value", result.get());
  }

  @Test
  @DisplayName("should create instance with empty map")
  void shouldCreateInstanceWithEmptyMap() {
    MapConfiguration config = new MapConfiguration(Collections.emptyMap());

    assertTrue(config.get("missing", String.class).isEmpty());
  }
}
