package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.github.liana.config.api.PlaceholderBuilder;
import io.github.liana.config.api.Placeholders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaceholdersTest {

  @Test
  @DisplayName("should return a non-null builder instance")
  void shouldReturnNonNullBuilderInstance() {
    PlaceholderBuilder builder = Placeholders.builder();
    assertNotNull(builder);
  }

  @Test
  @DisplayName("should return a new instance on each builder() call")
  void shouldReturnNewInstanceOnEachBuilderCall() {
    PlaceholderBuilder first = Placeholders.builder();
    PlaceholderBuilder second = Placeholders.builder();
    assertNotSame(first, second);
  }
}
