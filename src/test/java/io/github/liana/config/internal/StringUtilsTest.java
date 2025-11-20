package io.github.liana.config.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.liana.config.internal.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  @DisplayName("should return default value when input is blank")
  void shouldReturnDefaultWhenInputIsBlank() {
    assertEquals("default", StringUtils.defaultIfBlank(null, "default"));
    assertEquals("default", StringUtils.defaultIfBlank("", "default"));
    assertEquals("default", StringUtils.defaultIfBlank("   ", "default"));
  }

  @Test
  @DisplayName("should return original value when input is not blank")
  void shouldReturnOriginalWhenInputIsNotBlank() {
    assertEquals("hello", StringUtils.defaultIfBlank("hello", "default"));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when requiring non-blank value and input is null")
  void shouldThrowWhenRequireNonBlankReceivesNull() {
    assertThrows(IllegalArgumentException.class,
        () -> StringUtils.requireNonBlank(null, "value must not be blank"));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when requiring non-blank value and input is only spaces")
  void shouldThrowWhenRequireNonBlankReceivesOnlySpaces() {
    assertThrows(IllegalArgumentException.class,
        () -> StringUtils.requireNonBlank("   ", "value must not be blank"));
  }

  @Test
  @DisplayName("should return same value when requiring non-blank value and input is valid")
  void shouldReturnValueWhenRequireNonBlankReceivesValidValue() {
    String result = StringUtils.requireNonBlank("default", "value must not be blank");
    assertEquals("default", result);
  }

  @Test
  @DisplayName("should detect blank strings")
  void shouldDetectBlankStrings() {
    assertTrue(StringUtils.isBlank(null));
    assertTrue(StringUtils.isBlank(""));
    assertTrue(StringUtils.isBlank("   "));
  }

  @Test
  @DisplayName("should detect non-blank strings")
  void shouldDetectNonBlankStrings() {
    assertFalse(StringUtils.isBlank("default"));
    assertFalse(StringUtils.isBlank(" default "));
  }

  @Test
  @DisplayName("should compare strings ignoring case")
  void shouldCompareStringsIgnoringCase() {
    assertTrue(StringUtils.equalsIgnoreCase("default", "DEFAULT"));
    assertTrue(StringUtils.equalsIgnoreCase(null, null));
    assertFalse(StringUtils.equalsIgnoreCase("default", null));
    assertFalse(StringUtils.equalsIgnoreCase(null, "default"));
    assertFalse(StringUtils.equalsIgnoreCase("default", "xyz"));
  }
}
