package io.github.liana.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LinkedConfigSetTest {

  @Test
  @DisplayName("should construct empty set with default constructor")
  void shouldConstructEmptySetWithDefaultConstructor() {
    LinkedConfigSet set = new LinkedConfigSet();

    assertTrue(set.isEmpty());
  }

  @Test
  @DisplayName("should throw NullPointerException when constructing with null collection")
  void shouldThrowWhenConstructingWithNullCollection() {
    assertThrows(NullPointerException.class, () -> new LinkedConfigSet(null));
  }

  @Test
  @DisplayName("should ignore null and blank elements when constructing from collection")
  void shouldIgnoreNullAndBlankElementsOnConstruction() {
    LinkedConfigSet set = new LinkedConfigSet(Arrays.asList("alpha", null, " ", "beta"));

    assertEquals(2, set.size());
    assertEquals(Set.of("alpha", "beta"), set);
  }

  @Test
  @DisplayName("should preserve insertion order when constructing from collection")
  void shouldPreserveInsertionOrderOnConstruction() {
    List<String> input = Arrays.asList("alpha", "beta", "gamma");
    LinkedConfigSet set = new LinkedConfigSet(input);

    assertIterableEquals(input, set);
  }

  @Test
  @DisplayName("should add non-blank value to set")
  void shouldAddNonBlankValue() {
    LinkedConfigSet set = new LinkedConfigSet();

    boolean added = set.add("alpha");

    assertTrue(added);
    assertTrue(set.contains("alpha"));
  }

  @Test
  @DisplayName("should not add null value to set")
  void shouldNotAddNullValue() {
    LinkedConfigSet set = new LinkedConfigSet();

    boolean added = set.add(null);

    assertFalse(added);
    assertTrue(set.isEmpty());
  }

  @Test
  @DisplayName("should not add blank value to set")
  void shouldNotAddBlankValue() {
    LinkedConfigSet set = new LinkedConfigSet();

    boolean added = set.add("   ");

    assertFalse(added);
    assertTrue(set.isEmpty());
  }

  @Test
  @DisplayName("should return false when adding duplicate value")
  void shouldReturnFalseWhenAddingDuplicateValue() {
    LinkedConfigSet set = new LinkedConfigSet();
    set.add("alpha");

    boolean addedAgain = set.add("alpha");

    assertFalse(addedAgain);
    assertEquals(1, set.size());
  }

  @Test
  @DisplayName("should add multiple valid values with addAll")
  void shouldAddMultipleValidValuesWithAddAll() {
    LinkedConfigSet set = new LinkedConfigSet();

    boolean modified = set.addAll(Arrays.asList("alpha", "beta", "gamma"));

    assertTrue(modified);
    assertEquals(3, set.size());
    assertTrue(set.containsAll(List.of("alpha", "beta", "gamma")));
  }

  @Test
  @DisplayName("should ignore null and blank values in addAll")
  void shouldIgnoreNullAndBlankValuesInAddAll() {
    LinkedConfigSet set = new LinkedConfigSet();

    boolean modified = set.addAll(Arrays.asList("alpha", null, " ", "beta"));

    assertTrue(modified);
    assertEquals(Set.of("alpha", "beta"), set);
  }

  @Test
  @DisplayName("should return false from addAll when all values are null or blank")
  void shouldReturnFalseWhenAllValuesIgnoredInAddAll() {
    LinkedConfigSet set = new LinkedConfigSet();

    boolean modified = set.addAll(Arrays.asList(null, " ", "\n"));

    assertFalse(modified);
    assertTrue(set.isEmpty());
  }

  @Test
  @DisplayName("should throw NullPointerException when addAll receives null collection")
  void shouldThrowWhenAddAllReceivesNull() {
    LinkedConfigSet set = new LinkedConfigSet();

    assertThrows(NullPointerException.class, () -> set.addAll(null));
  }
}
