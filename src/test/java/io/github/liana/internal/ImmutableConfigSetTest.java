package io.github.liana.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImmutableConfigSetTest {

  @Test
  @DisplayName("should return an empty ImmutableConfigSet when empty() is called")
  void shouldReturnEmptyImmutableConfigSetWhenEmptyCalled() {
    ImmutableConfigSet emptySet = ImmutableConfigSet.empty();

    assertTrue(emptySet.isEmpty());
    assertTrue(emptySet.toSet().isEmpty());
  }

  @Test
  @DisplayName("should wrap provided set as unmodifiable when of() is called")
  void shouldWrapProvidedSetAsUnmodifiableWhenOfCalled() {
    Set<String> original = new LinkedHashSet<>();
    original.add("alpha");
    original.add("bata");

    ImmutableConfigSet configSet = ImmutableConfigSet.of(original);

    assertFalse(configSet.isEmpty());
    assertEquals(2, configSet.toSet().size());
    assertThrows(UnsupportedOperationException.class, () -> configSet.toSet().add("stage"));
  }

  @Test
  @DisplayName("should throw NullPointerException when set is null in of()")
  void shouldThrowNullPointerExceptionWhenSetIsNull() {
    assertThrows(NullPointerException.class, () -> ImmutableConfigSet.of(null));
  }

  @Test
  @DisplayName("should return true for equals() when sets contain the same elements")
  void shouldReturnTrueForEqualsWhenSetsContainSameElements() {
    ImmutableConfigSet set1 = ImmutableConfigSet.of(Set.of("alpha", "beta"));
    ImmutableConfigSet set2 = ImmutableConfigSet.of(Set.of("alpha", "beta"));

    assertEquals(set1, set2);
    assertEquals(set1.hashCode(), set2.hashCode());
  }

  @Test
  @DisplayName("should return false for equals() when sets contain different elements")
  void shouldReturnFalseForEqualsWhenSetsContainDifferentElements() {
    ImmutableConfigSet set1 = ImmutableConfigSet.of(Set.of("alpha"));
    ImmutableConfigSet set2 = ImmutableConfigSet.of(Set.of("beta"));

    assertNotEquals(set1, set2);
  }

  @Test
  @DisplayName("should return false for equals() when compared with null or different type")
  void shouldReturnFalseForEqualsWhenComparedWithNullOrDifferentType() {
    ImmutableConfigSet set = ImmutableConfigSet.of(Set.of("alpha"));

    assertNotEquals(set, null);
    assertNotEquals(set, "not a set");
  }

  @Test
  @DisplayName("should include class name and set contents in toString()")
  void shouldIncludeClassNameAndSetContentsInToString() {
    ImmutableConfigSet set = ImmutableConfigSet.of(Set.of("alpha", "beta"));
    String result = set.toString();

    assertTrue(result.contains("ImmutableConfigSet"));
    assertTrue(result.contains("alpha"));
    assertTrue(result.contains("beta"));
  }

  @Test
  @DisplayName("should return true for equals() when compared with itself")
  void shouldReturnTrueForEqualsWhenComparedWithItself() {
    ImmutableConfigSet set = ImmutableConfigSet.of(Set.of("alpha", "beta"));

    assertTrue(set.equals(set));
  }
}
