package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.liana.config.api.Placeholder;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultPlaceholderBuilderTest {

  @Test
  @DisplayName("should resolve placeholder using custom prefix")
  void shouldResolveWithCustomPrefix() {
    String prefix = "#{";
    Placeholder placeholder = new DefaultPlaceholderBuilder()
        .prefix(prefix)
        .suffix("}")
        .build();

    String input = "application-#{profile}";
    Optional<String> resolved = placeholder.replaceIfAllResolvable(input, key -> "dev");

    assertEquals(Optional.of("application-dev"), resolved);
  }

  @Test
  @DisplayName("should resolve placeholder using custom suffix")
  void shouldResolveWithCustomSuffix() {
    String suffix = "}}";
    Placeholder placeholder = new DefaultPlaceholderBuilder()
        .prefix("${")
        .suffix(suffix)
        .build();

    String input = "application-${profile}}";
    Optional<String> resolved = placeholder.replaceIfAllResolvable(input, key -> "dev");

    assertEquals(Optional.of("application-dev"), resolved);
  }

  @Test
  @DisplayName("should resolve placeholder with custom delimiter for default value")
  void shouldResolveWithCustomDelimiter() {
    String delimiter = "|";
    Placeholder placeholder = new DefaultPlaceholderBuilder()
        .delimiter(delimiter)
        .build();

    String input = "application-${profile|default}";
    Optional<String> resolved = placeholder.replaceIfAllResolvable(input, key -> null);

    assertEquals(Optional.of("application-default"), resolved);
  }

  @Test
  @DisplayName("should not resolve placeholder when escaped with custom escape character")
  void shouldNotResolveWithCustomEscapeChar() {
    char escapeChar = '#';
    Placeholder placeholder = new DefaultPlaceholderBuilder()
        .escapeChar(escapeChar)
        .build();

    String input = "application-#${profile}";
    Optional<String> resolved = placeholder.replaceIfAllResolvable(input, key -> "secret");

    assertEquals(Optional.of("application-${profile}"), resolved);
  }

  @Test
  @DisplayName("should resolve placeholder using custom source")
  void shouldResolveWithCustomSource() {
    Placeholder placeholder = new DefaultPlaceholderBuilder()
        .build();

    String input = "application-${profile}";
    Optional<String> resolved = placeholder.replaceIfAllResolvable(input, key -> "stage");

    assertEquals(Optional.of("application-stage"), resolved);
  }

  @Test
  @DisplayName("should build with all custom values and resolve correctly")
  void shouldBuildWithAllCustomValues() {
    PropertySource customSource = key -> "profile".equals(key) ? "dev" : null;

    Placeholder placeholder = new DefaultPlaceholderBuilder()
        .prefix("<<")
        .suffix(">>")
        .delimiter("::")
        .escapeChar('&')
        .build();

    String input = "application-<<profile>>-<<region::us-east-1>>-&<<zone>>";
    Optional<String> resolved = placeholder.replaceIfAllResolvable(input, customSource);

    assertEquals(Optional.of("application-dev-us-east-1-<<zone>>"), resolved);
  }
}
