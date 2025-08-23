package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.liana.internal.ImmutableConfigMap;
import io.github.liana.internal.ImmutableConfigSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultConfigResourceLocationTest {

  @Test
  @DisplayName("should construct with valid values and return them via getters")
  void shouldConstructAndReturnValues() {
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml", "secrets.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));
    final String PROVIDER = "classpath";
    var placeholder = mock(Placeholder.class);

    ConfigResourceLocation location = new DefaultConfigResourceLocation(
        PROVIDER,
        resourceNames,
        variables,
        true,
        placeholder
    );

    assertEquals(PROVIDER, location.getProvider());
    assertEquals(resourceNames, location.getResourceNames());
    assertEquals(variables, location.getVariables());
    assertTrue(location.isVerboseLogging());
    assertSame(placeholder, location.getPlaceholder());
  }

  @Test
  @DisplayName("should throw NullPointerException when provider is null")
  void shouldThrowWhenProviderIsNull() {
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));
    var placeholder = mock(Placeholder.class);

    assertThrows(NullPointerException.class, () ->
        new DefaultConfigResourceLocation(null, resourceNames, variables, false, placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when resourceNames is null")
  void shouldThrowWhenResourceNamesIsNull() {
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));
    var placeholder = mock(Placeholder.class);

    assertThrows(NullPointerException.class, () ->
        new DefaultConfigResourceLocation("classpath", null, variables, false, placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when variables is null")
  void shouldThrowWhenVariablesIsNull() {
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));
    var placeholder = mock(Placeholder.class);

    assertThrows(NullPointerException.class, () ->
        new DefaultConfigResourceLocation("classpath", resourceNames, null, false, placeholder)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when placeholderResolver is null")
  void shouldThrowWhenPlaceholderResolverIsNull() {
    var resourceNames = ImmutableConfigSet.of(Set.of("config.yml"));
    var variables = ImmutableConfigMap.of(Map.of("env", "prod"));

    assertThrows(NullPointerException.class, () ->
        new DefaultConfigResourceLocation("classpath", resourceNames, variables, false, null)
    );
  }
}
