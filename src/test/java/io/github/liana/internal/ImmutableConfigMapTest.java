package io.github.liana.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImmutableConfigMapTest {

  @Test
  @DisplayName("should create empty map when using empty()")
  void shouldCreateEmptyMapWhenUsingEmpty() {
    ImmutableConfigMap map = ImmutableConfigMap.empty();

    assertTrue(map.isEmpty());
    assertTrue(map.toMap().isEmpty());
  }

  @Test
  @DisplayName("should create immutable map with provided entries when using of()")
  void shouldCreateImmutableMapWithProvidedEntriesWhenUsingOf() {
    Map<String, String> source = new HashMap<>();
    source.put("k1", "v1");
    source.put("k2", "v2");

    ImmutableConfigMap map = ImmutableConfigMap.of(source);

    assertFalse(map.isEmpty());
    assertEquals("v1", map.get("k1"));
    assertEquals("v2", map.get("k2"));
    assertEquals(source, map.toMap());
  }

  @Test
  @DisplayName("should throw NullPointerException when map passed to of() is null")
  void shouldThrowWhenMapPassedToOfIsNull() {
    assertThrows(NullPointerException.class, () -> ImmutableConfigMap.of(null));
  }

  @Test
  @DisplayName("should return value when key exists")
  void shouldReturnValueWhenKeyExists() {
    Map<String, String> source = Map.of("k1", "v1");
    ImmutableConfigMap map = ImmutableConfigMap.of(source);

    assertEquals("v1", map.get("k1"));
  }

  @Test
  @DisplayName("should return null when key does not exist")
  void shouldReturnNullWhenKeyDoesNotExist() {
    ImmutableConfigMap map = ImmutableConfigMap.empty();

    assertNull(map.get("missingKey"));
  }

  @Test
  @DisplayName("should throw NullPointerException when key is null in get()")
  void shouldThrowWhenKeyIsNullInGet() {
    ImmutableConfigMap map = ImmutableConfigMap.empty();

    assertThrows(NullPointerException.class, () -> map.get(null));
  }

  @Test
  @DisplayName("should return true for isEmpty() when no entries present")
  void shouldReturnTrueForIsEmptyWhenNoEntriesPresent() {
    ImmutableConfigMap map = ImmutableConfigMap.empty();

    assertTrue(map.isEmpty());
  }

  @Test
  @DisplayName("should return false for isEmpty() when entries are present")
  void shouldReturnFalseForIsEmptyWhenEntriesArePresent() {
    ImmutableConfigMap map = ImmutableConfigMap.of(Map.of("k1", "v1"));

    assertFalse(map.isEmpty());
  }

  @Test
  @DisplayName("should return unmodifiable map from toMap()")
  void shouldReturnUnmodifiableMapFromToMap() {
    ImmutableConfigMap map = ImmutableConfigMap.of(Map.of("k1", "v1"));

    assertThrows(UnsupportedOperationException.class, () -> map.toMap().put("k2", "v2"));
  }

  @Test
  @DisplayName("should be equal when comparing to itself")
  void shouldBeEqualWhenComparingToItself() {
    ImmutableConfigMap map = ImmutableConfigMap.of(Map.of("k1", "v1"));

    assertEquals(map, map);
  }

  @Test
  @DisplayName("should be equal when maps have same content")
  void shouldBeEqualWhenMapsHaveSameContent() {
    ImmutableConfigMap map1 = ImmutableConfigMap.of(Map.of("k1", "v1"));
    ImmutableConfigMap map2 = ImmutableConfigMap.of(Map.of("k1", "v1"));

    assertEquals(map1, map2);
    assertEquals(map1.hashCode(), map2.hashCode());
  }

  @Test
  @DisplayName("should not be equal when maps have different content")
  void shouldNotBeEqualWhenMapsHaveDifferentContent() {
    ImmutableConfigMap map1 = ImmutableConfigMap.of(Map.of("k1", "v1"));
    ImmutableConfigMap map2 = ImmutableConfigMap.of(Map.of("k1", "v2"));

    assertNotEquals(map1, map2);
  }

  @Test
  @DisplayName("should not be equal when compared with object of different type")
  void shouldNotBeEqualWhenComparedWithDifferentType() {
    ImmutableConfigMap map = ImmutableConfigMap.of(Map.of("k1", "v1"));

    assertNotEquals(map, "some string");
  }

  @Test
  @DisplayName("should return non-empty string representation from toString()")
  void shouldReturnNonEmptyStringRepresentationFromToString() {
    ImmutableConfigMap map = ImmutableConfigMap.of(Map.of("k1", "v1"));

    String result = map.toString();

    assertTrue(result.contains("ImmutableConfigMap"));
    assertTrue(result.contains("k1"));
  }
}
