package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultPlaceholderTest {

  @Mock
  PropertySource envSource;

  @Mock
  PropertySource mapSource;

  private Placeholder resolver;

  @BeforeEach
  void setUp() {
    resolver = new DefaultPlaceholder("${", "}", ":", '\\',
        List.of(envSource, mapSource));
  }

  @Test
  @DisplayName("should resolve placeholder using environment source")
  void resolvesPlaceholderUsingEnvSourceOnly() {
    when(envSource.get("profile")).thenReturn("dev");

    String input = "application-${profile}";
    Optional<String> result = resolver.replaceIfAllResolvable(input);

    assertEquals(Optional.of("application-dev"), result);
    verify(envSource).get("profile");
    verifyNoInteractions(mapSource);
  }

  @Test
  @DisplayName("should resolve placeholder from map when not found in environment")
  void shouldReplaceSimplePlaceholder() {
    when(mapSource.get("profile")).thenReturn("dev");

    final var TEMPLATE = "application-${profile}";

    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application-dev"), result);
    verify(envSource).get("profile");
    verify(mapSource).get("profile");
  }

  @Test
  @DisplayName("should fallback when key missing and default present")
  void shouldFallbackToDefaultWhenKeyMissing() {
    final var TEMPLATE = "application-${profile:default}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application-default"), result);
  }

  @Test
  @DisplayName("should leave unresolved if key missing and no fallback")
  void shouldReturnEmptyOptionalIfUnresolved() {
    final var TEMPLATE = "application-${profile}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.empty(), result);
  }

  @Test
  @DisplayName("should handle nested placeholders")
  void shouldHandleNestedPlaceholders() {
    when(mapSource.get("profile")).thenReturn("${environment}");
    when(mapSource.get("environment")).thenReturn("dev");

    final var TEMPLATE = "application-${profile}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application-dev"), result);
  }

  @Test
  @DisplayName("should throw on circular references")
  void shouldThrowOnCircularReferences() {
    when(mapSource.get("profile")).thenReturn("${environment}");
    when(mapSource.get("environment")).thenReturn("${profile}");

    final var TEMPLATE = "application-${profile}";

    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> resolver.replaceIfAllResolvable(TEMPLATE));

    assertTrue(ex.getMessage().contains("Circular reference detected for key"));
  }

  @Test
  @DisplayName("should allow empty fallback as valid value")
  void shouldAllowEmptyFallbackAsValid() {
    final var TEMPLATE = "application-${profile:}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application-"), result);
  }

  @Test
  @DisplayName("should keep literal if placeholder is malformed")
  void shouldReturnOriginalIfMalformed() {
    final var TEMPLATE = "application-${}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.empty(), result);
  }

  @Test
  @DisplayName("should resolve multiple placeholders")
  void shouldResolveMultiplePlaceholders() {
    when(mapSource.get("profile")).thenReturn("dev");
    when(mapSource.get("region")).thenReturn("us-east-1");

    final var TEMPLATE = "application-${profile}-${region}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application-dev-us-east-1"), result);
  }

  @Test
  @DisplayName("should resolve unescaped placeholders and ignore escaped ones")
  void shouldResolveUnescapedPlaceholdersAndIgnoreEscapedOnes() {
    when(mapSource.get("profile")).thenReturn("dev");

    final var TEMPLATE = "application-${profile}-\\${region}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application-dev-${region}"), result);
  }

  @Test
  @DisplayName("should return same string if no placeholders")
  void shouldReturnSameIfNoPlaceholders() {
    final var TEMPLATE = "application";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application"), result);
  }

  @Test
  @DisplayName("should return empty when template is blank")
  void shouldReturnBlankTemplate() {
    Optional<String> result = resolver.replaceIfAllResolvable("   ");
    assertEquals(Optional.of("   "), result);
  }

  @Test
  @DisplayName("should throw NullPointerException when template is null")
  void shouldThrowNullPointerExceptionWhenTemplateIsNull() {
    NullPointerException ex = assertThrows(NullPointerException.class,
        () -> resolver.replaceIfAllResolvable(null));

    assertTrue(ex.getMessage().contains("template must not be null"));
  }

  @Test
  @DisplayName("should resolve deeply nested structure")
  void shouldResolveDeeplyNested() {
    when(mapSource.get("profile")).thenReturn("prod");
    when(mapSource.get("region-prod")).thenReturn("us-east-1");
    when(mapSource.get("zone-us-east-1")).thenReturn("zone-a");

    final var TEMPLATE = "application-${profile}-${region-${profile}}-${zone-${region-${profile}}}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application-prod-us-east-1-zone-a"), result);
  }

  @Test
  @DisplayName("should allow custom delimiters")
  void shouldAllowCustomDelimiters() {
    PropertySource mockSource = mock(PropertySource.class);
    when(mockSource.get("profile")).thenReturn("dev");

    var customResolver = new DefaultPlaceholder("{{", "}}", "|", '\\',
        List.of(mockSource));

    final var TEMPLATE = "application-{{profile|default}}";

    Optional<String> result = customResolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application-dev"), result);
  }

  @Test
  @DisplayName("should handle variables map being null")
  void shouldHandleNullMap() {
    final var TEMPLATE = "application-${profile:default}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);

    assertEquals(Optional.of("application-default"), result);
  }

  @Test
  @DisplayName("should handle suffix after placeholder correctly")
  void shouldHandleSuffixAfterPlaceholder() {
    when(mapSource.get("profile")).thenReturn("dev");
    final var TEMPLATE = "application-${profile}}";
    Optional<String> result = resolver.replaceIfAllResolvable(TEMPLATE);
    assertEquals(Optional.of("application-dev}"), result);
  }

  @Test
  @DisplayName("should throw when prefix is null")
  void shouldThrowWhenPrefixIsNull() {
    List<PropertySource> sources = List.of(mock(PropertySource.class));
    assertThrows(IllegalArgumentException.class, () ->
        new DefaultPlaceholder(null, "}", ":", '\\', sources));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n"})
  @DisplayName("should throw when prefix is blank")
  void shouldThrowWhenPrefixIsBlank(String invalidPrefix) {
    List<PropertySource> sources = List.of(mock(PropertySource.class));

    assertThrows(IllegalArgumentException.class, () ->
        new DefaultPlaceholder(invalidPrefix, "}", ":", '\\', sources));
  }

  @Test
  @DisplayName("should throw when suffix is null")
  void shouldThrowWhenSuffixIsNull() {
    List<PropertySource> sources = List.of(mock(PropertySource.class));

    assertThrows(IllegalArgumentException.class, () ->
        new DefaultPlaceholder("${", null, ":", '\\', sources));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n"})
  @DisplayName("should throw when suffix is blank")
  void shouldThrowWhenSuffixIsBlank(String invalidSuffix) {
    List<PropertySource> sources = List.of(mock(PropertySource.class));

    assertThrows(IllegalArgumentException.class, () ->
        new DefaultPlaceholder("${", invalidSuffix, ":", '\\', sources));
  }

  @Test
  @DisplayName("should throw when delimiter is null")
  void shouldThrowWhenDelimiterIsNull() {
    List<PropertySource> sources = List.of(mock(PropertySource.class));

    assertThrows(IllegalArgumentException.class, () ->
        new DefaultPlaceholder("${", "}", null, '\\', sources));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n"})
  @DisplayName("should throw when delimiter is blank")
  void shouldThrowWhenDelimiterIsBlank(String invalidDelimiter) {
    List<PropertySource> sources = List.of(mock(PropertySource.class));

    assertThrows(IllegalArgumentException.class, () ->
        new DefaultPlaceholder("${", "}", invalidDelimiter, '\\', sources));
  }

  @Test
  @DisplayName("should throw when sources is null")
  void shouldThrowWhenSourcesIsNull() {
    assertThrows(NullPointerException.class, () ->
        new DefaultPlaceholder("${", "}", ":", '\\', null));
  }

  @Test
  @DisplayName("should return empty when sources is empty")
  void shouldReturnEmptyWhenSourcesIsEmpty() {
    var customResolver = new DefaultPlaceholder("${", "}", ":", '\\', List.of());
    final var TEMPLATE = "application-${profile}}";
    Optional<String> result = customResolver.replaceIfAllResolvable(TEMPLATE);
    assertEquals(Optional.empty(), result);
  }
}
