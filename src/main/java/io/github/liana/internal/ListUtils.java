package io.github.liana.internal;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ListUtils {

  private ListUtils() {
  }

  public static <E> List<E> immutableCopyOf(Collection<E> input, String message) {
    requireNonNull(input, message);

    if (input.isEmpty()) {
      return Collections.emptyList();
    }

    List<E> copy = new ArrayList<>(input.size());
    for (E item : input) {
      requireNonNull(item, "list must not contain null elements");
      copy.add(item);
    }

    return Collections.unmodifiableList(copy);
  }
}
