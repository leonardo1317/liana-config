package io.github.liana.internal;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable wrapper for a {@link Set} of {@link String} values.
 *
 * <p>This class provides a simple, read-only view of a {@link Set}. It guarantees immutability
 * by wrapping the provided set in an unmodifiable collection.
 *
 * <p>Instances of this class can be created using {@link #empty()} or {@link #of(Set)}.
 *
 * <h2>Examples</h2>
 *
 * <pre>{@code
 * ImmutableConfigSet empty = ImmutableConfigSet.empty();
 * ImmutableConfigSet configs = ImmutableConfigSet.of(Set.of("key1", "key2"));
 * boolean isEmpty = configs.isEmpty(); // false
 * }</pre>
 */
public class ImmutableConfigSet {

  private final Set<String> set;

  private ImmutableConfigSet(Set<String> set) {
    this.set = Collections.unmodifiableSet(set);
  }

  /**
   * Creates an empty {@code ImmutableConfigSet}.
   *
   * @return an empty {@code ImmutableConfigSet}
   */
  public static ImmutableConfigSet empty() {
    return new ImmutableConfigSet(Collections.emptySet());
  }

  /**
   * Creates an {@code ImmutableConfigSet} containing the specified set of strings.
   *
   * @param set the set to wrap; must not be {@code null}
   * @return an {@code ImmutableConfigSet} with the given values
   * @throws NullPointerException if {@code set} is {@code null}
   */
  public static ImmutableConfigSet of(Set<String> set) {
    return new ImmutableConfigSet(new LinkedConfigSet(set));
  }

  /**
   * Returns {@code true} if this set contains no elements.
   *
   * @return {@code true} if empty; {@code false} otherwise
   */
  public boolean isEmpty() {
    return set.isEmpty();
  }

  /**
   * Returns the underlying set as an unmodifiable view.
   *
   * @return the internal unmodifiable set
   */
  public Set<String> toSet() {
    return set;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ImmutableConfigSet that)) {
      return false;
    }
    return Objects.equals(set, that.set);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(set);
  }

  @Override
  public String toString() {
    return "ImmutableConfigSet" + set.toString();
  }
}
