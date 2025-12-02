package io.github.liana.config.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.liana.config.internal.LinkedConfigMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LinkedConfigMapTest {

  @Test
  @DisplayName("should construct empty map")
  void shouldConstructEmptyMap() {
    LinkedConfigMap map = new LinkedConfigMap();
    assertTrue(map.isEmpty());
  }

  @Test
  @DisplayName("should initialize with entries from another map")
  void shouldInitializeWithEntriesFromAnotherMap() {
    Map<String, String> source = Map.of("k1", "v1", "k2", "v2");
    LinkedConfigMap map = new LinkedConfigMap(source);

    assertEquals(2, map.size());
    assertEquals("v1", map.get("k1"));
    assertEquals("v2", map.get("k2"));
  }

  @Test
  @DisplayName("should throw when initializing with null map")
  void shouldThrowWhenInitializingWithNullMap() {
    assertThrows(NullPointerException.class, () -> new LinkedConfigMap(null));
  }

  @Test
  @DisplayName("should throw when map contains null key")
  void shouldThrowWhenMapContainsNullKey() {
    Map<String, String> invalid = new LinkedHashMap<>();
    invalid.put(null, "v1");
    assertThrows(IllegalArgumentException.class, () -> new LinkedConfigMap(invalid));
  }

  @Test
  @DisplayName("should throw when map contains blank key")
  void shouldThrowWhenMapContainsBlankKey() {
    Map<String, String> invalid = Map.of("  ", "v1");
    assertThrows(IllegalArgumentException.class, () -> new LinkedConfigMap(invalid));
  }

  @Test
  @DisplayName("should throw when map contains null value")
  void shouldThrowWhenMapContainsNullValue() {
    Map<String, String> invalid = new LinkedHashMap<>();
    invalid.put("k1", null);
    assertThrows(NullPointerException.class, () -> new LinkedConfigMap(invalid));
  }

  @Test
  @DisplayName("should allow blank values")
  void shouldAllowBlankValues() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "  ");
    assertEquals("  ", map.get("k1"));
  }

  @Test
  @DisplayName("should put valid key-value pair")
  void shouldPutValidKeyValuePair() {
    LinkedConfigMap map = new LinkedConfigMap();
    String result = map.put("k1", "v1");
    assertNull(result);
    assertEquals("v1", map.get("k1"));
  }

  @Test
  @DisplayName("should throw when putting null key")
  void shouldThrowWhenPuttingNullKey() {
    LinkedConfigMap map = new LinkedConfigMap();
    assertThrows(IllegalArgumentException.class, () -> map.put(null, "v1"));
  }

  @Test
  @DisplayName("should throw when putting null value")
  void shouldThrowWhenPuttingNullValue() {
    LinkedConfigMap map = new LinkedConfigMap();
    assertThrows(NullPointerException.class, () -> map.put("k1", null));
  }

  @Test
  @DisplayName("should putAll preserving order and validating entries")
  void shouldPutAllPreservingOrderAndValidatingEntries() {
    LinkedConfigMap map = new LinkedConfigMap();
    Map<String, String> source = new LinkedHashMap<>();
    source.put("k1", "v1");
    source.put("k2", "v2");

    map.putAll(source);
    assertIterableEquals(source.keySet(), map.keySet());
  }

  @Test
  @DisplayName("should throw when putAll receives null")
  void shouldThrowWhenPutAllWithNullMap() {
    LinkedConfigMap map = new LinkedConfigMap();
    assertThrows(NullPointerException.class, () -> map.putAll(null));
  }

  @Test
  @DisplayName("should putIfAbsent only if key is absent")
  void shouldPutIfAbsentOnlyIfKeyIsAbsent() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");
    map.putIfAbsent("k1", "newValue");
    assertEquals("v1", map.get("k1"));
  }

  @Test
  @DisplayName("should throw when putIfAbsent with null key")
  void shouldThrowWhenPutIfAbsentWithNullKey() {
    LinkedConfigMap map = new LinkedConfigMap();
    assertThrows(IllegalArgumentException.class, () -> map.putIfAbsent(null, "v1"));
  }

  @Test
  @DisplayName("should replace value when key exists")
  void shouldReplaceValueWhenKeyExists() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");
    String old = map.replace("k1", "new");
    assertEquals("v1", old);
    assertEquals("new", map.get("k1"));
  }

  @Test
  @DisplayName("should not replace value when key does not exist")
  void shouldNotReplaceValueWhenKeyDoesNotExist() {
    LinkedConfigMap map = new LinkedConfigMap();
    assertNull(map.replace("k1", "v1"));
  }

  @Test
  @DisplayName("should replace value when old value matches")
  void shouldReplaceValueWhenOldValueMatches() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");
    boolean replaced = map.replace("k1", "v1", "new");
    assertTrue(replaced);
    assertEquals("new", map.get("k1"));
  }

  @Test
  @DisplayName("should not replace value when old value does not match")
  void shouldNotReplaceValueWhenOldValueDoesNotMatch() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");
    boolean replaced = map.replace("k1", "other", "new");
    assertFalse(replaced);
    assertEquals("v1", map.get("k1"));
  }

  @Test
  @DisplayName("should compute new value and validate it for compute")
  void shouldComputeNewValueAndValidateIt() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");
    String result = map.compute("k1", (k, v) -> v + "_computed");
    assertEquals("v1_computed", result);
    assertEquals("v1_computed", map.get("k1"));
  }

  @Test
  @DisplayName("should remove entry when compute returns null for present key")
  void shouldRemoveEntryWhenComputeReturnsNullForPresentKey() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");

    String result = map.compute("k1", (k, v) -> null);

    assertNull(result, "compute should return null when remapping function returns null");
    assertFalse(map.containsKey("k1"),
        "entry should be removed when remapping function returns null");
  }

  @Test
  @DisplayName("should not add entry when compute returns null for absent key")
  void shouldNotAddWhenComputeReturnsNullForAbsentKey() {
    LinkedConfigMap map = new LinkedConfigMap();

    String result = map.compute("k1", (k, v) -> null);

    assertNull(result);
    assertFalse(map.containsKey("k1"));
  }

  @Test
  @DisplayName("should throw when compute with blank key and remapping returns non-null")
  void shouldThrowWhenComputeWithBlankKeyAndResultNonNull() {
    LinkedConfigMap map = new LinkedConfigMap();

    assertThrows(IllegalArgumentException.class,
        () -> map.compute("  ", (k, v) -> "value"));
  }

  @Test
  @DisplayName("should return null when computeIfAbsent mapping returns null")
  void shouldReturnNullWhenComputeIfAbsentMappingReturnsNull() {
    LinkedConfigMap map = new LinkedConfigMap();

    String result = map.computeIfAbsent("k1", k -> null);

    assertNull(result);
    assertFalse(map.containsKey("k1"));
  }

  @Test
  @DisplayName("should not call mapping when computeIfAbsent key already present")
  void shouldNotCallMappingWhenComputeIfAbsentKeyPresent() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "existing");

    String result = map.computeIfAbsent("k1", k -> {
      fail("mapping function should not be called when key present");
      return "computed";
    });

    assertEquals("existing", result);
  }

  @Test
  @DisplayName("should throw when computeIfAbsent with blank key and mapping returns non-null")
  void shouldThrowWhenComputeIfAbsentWithBlankKeyAndNonNullMapping() {
    LinkedConfigMap map = new LinkedConfigMap();

    assertThrows(IllegalArgumentException.class,
        () -> map.computeIfAbsent("  ", k -> "value"));
  }

  @Test
  @DisplayName("should remove entry when computeIfPresent remapping returns null")
  void shouldRemoveEntryWhenComputeIfPresentReturnsNull() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");

    String result = map.computeIfPresent("k1", (k, v) -> null);

    assertNull(result);
    assertFalse(map.containsKey("k1"));
  }

  @Test
  @DisplayName("should compute if present when key exists")
  void shouldComputeIfPresentWhenKeyExists() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");

    String result = map.computeIfPresent("k1", (k, v) -> v + "_updated");

    assertEquals("v1_updated", result);
    assertEquals("v1_updated", map.get("k1"));
  }

  @Test
  @DisplayName("should merge values using remapping function")
  void shouldMergeValuesUsingRemappingFunction() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");

    String result = map.merge("k1", "v2", (oldVal, newVal) -> oldVal + "+" + newVal);

    assertEquals("v1+v2", result);
    assertEquals("v1+v2", map.get("k1"));
  }

  @Test
  @DisplayName("should remove entry when merge remapping returns null")
  void shouldRemoveEntryWhenMergeRemappingReturnsNull() {
    LinkedConfigMap map = new LinkedConfigMap();
    map.put("k1", "v1");

    String result = map.merge("k1", "v2", (oldVal, newVal) -> null);

    assertNull(result);
    assertFalse(map.containsKey("k1"));
  }

  @Test
  @DisplayName("should add entry when merge key absent and value non-null")
  void shouldAddWhenMergeKeyAbsentAndValueNonNull() {
    LinkedConfigMap map = new LinkedConfigMap();

    String result = map.merge("k1", "v1", (oldVal, newVal) -> {
      fail("remapping should not be called when key absent");
      return null;
    });

    assertEquals("v1", result);
    assertEquals("v1", map.get("k1"));
  }

  @Test
  @DisplayName("should throw when merge with blank key and result non-null")
  void shouldThrowWhenMergeWithBlankKeyAndResultNonNull() {
    LinkedConfigMap map = new LinkedConfigMap();

    assertThrows(IllegalArgumentException.class,
        () -> map.merge("  ", "v", (oldVal, newVal) -> newVal));
  }
}
