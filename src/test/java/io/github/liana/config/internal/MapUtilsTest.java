package io.github.liana.config.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.liana.config.internal.MapUtils;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MapUtilsTest {

  @Test
  @DisplayName("should throw NullPointerException when entries array is null")
  void shouldThrowWhenEntriesArrayIsNull() {
    assertThrows(NullPointerException.class, () -> MapUtils.of((Object[]) null));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when entries array has odd length")
  void shouldThrowWhenEntriesArrayHasOddLength() {
    String[] oddArray = {"key1", "value1", "key2"};
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> MapUtils.of(oddArray));

    assertEquals("Missing value for key: key2", exception.getMessage());
  }

  @Test
  @DisplayName("should return empty map when entries array is empty")
  void shouldReturnEmptyMapWhenEntriesArrayIsEmpty() {
    Map<String, String> result = MapUtils.of(new String[0]);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should return map with single entry when one key-value pair is provided")
  void shouldReturnSingleEntryMap() {
    String[] entries = {"key1", "value1"};
    Map<String, String> result = MapUtils.of(entries);

    assertEquals(1, result.size());
    assertEquals("value1", result.get("key1"));
  }

  @Test
  @DisplayName("should not guarantee insertion order")
  void shouldNotGuaranteeInsertionOrder() {
    String[] entries = {"k1", "v1", "k2", "v2", "k3", "v3"};
    Map<String, String> result = MapUtils.of(entries);

    assertEquals(3, result.size());
    assertEquals("v1", result.get("k1"));
    assertEquals("v2", result.get("k2"));
    assertEquals("v3", result.get("k3"));
  }

  @Test
  @DisplayName("should override duplicate keys with last occurrence value")
  void shouldOverrideDuplicateKeys() {
    String[] entries = {"key1", "value1", "key1", "value2"};
    Map<String, String> result = MapUtils.of(entries);

    assertEquals(1, result.size());
    assertEquals("value2", result.get("key1"));
  }

  @Test
  @DisplayName("should throw UnsupportedOperationException when attempting to modify the map")
  void shouldBeImmutable() {
    String[] entries = {"key1", "value1"};
    Map<String, String> result = MapUtils.of(entries);

    assertThrows(UnsupportedOperationException.class, () -> result.put("key2", "value2"));
    assertThrows(UnsupportedOperationException.class, () -> result.remove("key1"));
    assertThrows(UnsupportedOperationException.class, result::clear);
  }
}
