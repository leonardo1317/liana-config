package io.github.liana.config.core;

import static io.github.liana.config.internal.StringUtils.isBlank;
import static io.github.liana.config.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.Placeholder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Provides methods to resolve placeholders within strings using environment variables or a provided
 * map of values.
 *
 * <p>Supports placeholders in the format {@code ${key}} and {@code ${key:default}}. Resolution is
 * attempted in the following order: environment variables, the provided value map, and finally the
 * default value (if specified).
 *
 * <p>Resolution is recursive. If a resolved value contains additional placeholders, they are also
 * resolved. Circular references result in an {@link IllegalStateException}.
 *
 * <p>This class is immutable and supports configurable prefix, suffix, delimiter, and escape
 * characters.
 */
public final class DefaultPlaceholder implements Placeholder {

  private final List<PropertySource> sources;
  private final String prefix;
  private final String suffix;
  private final String delimiter;
  private final char escapeChar;

  public DefaultPlaceholder(String prefix, String suffix, String delimiter, char escapeChar) {
    this(prefix, suffix, delimiter, escapeChar, List.of(PropertySources.fromEnv()));
  }

  /**
   * Constructs a {@code DefaultPlaceholder} with custom prefix, suffix, delimiter, escape
   * character, and a list of placeholder sources.
   *
   * <p>The provided {@code sources} are queried after environment variables when resolving
   * placeholders.
   *
   * @param prefix     the prefix string used to identify placeholders; must not be null or blank
   * @param suffix     the suffix string used to identify placeholders; must not be null or blank
   * @param delimiter  the string that separates key and default value; must not be null or blank
   * @param escapeChar the character used to escape the placeholder prefix
   * @param sources    additional property sources to query; must not be null and must not contain
   *                   null elements
   * @throws NullPointerException     if any argument is null
   * @throws IllegalArgumentException if {@code prefix}, {@code suffix}, or {@code delimiter} are
   *                                  blank
   */
  public DefaultPlaceholder(String prefix, String suffix, String delimiter, char escapeChar,
      List<PropertySource> sources) {
    this.prefix = requireNonBlank(prefix, "prefix must not be null or blank");
    this.suffix = requireNonBlank(suffix, "suffix must not be null or blank");
    this.delimiter = requireNonBlank(delimiter, "delimiter must not be null or blank");
    this.escapeChar = escapeChar;
    this.sources = List.copyOf(requireNonNull(sources, "sources must not be null"));
  }

  /**
   * Resolves all placeholders in the given template if and only if all placeholders can be resolved
   * from environment variables or provided sources.
   *
   * <p>Placeholders that cannot be resolved will cause this method to return an empty
   * {@link Optional}.
   *
   * @param template the string possibly containing placeholders; must not be null
   * @return an {@link Optional} containing the resolved string if all placeholders were resolved;
   * otherwise an empty {@link Optional}
   * @throws NullPointerException if {@code template} is null
   */
  @Override
  public Optional<String> replaceIfAllResolvable(String template, PropertySource... extraSources) {
    requireNonNull(template, "template must not be null");

    if (isBlank(template) || !template.contains(prefix)) {
      return Optional.of(template);
    }

    List<PropertySource> mergedSources = mergeSources(extraSources);
    if (mergedSources.isEmpty()) {
      return Optional.empty();
    }

    var unresolved = new HashSet<String>();
    String resolved = replace(template, mergedSources, new HashSet<>(), unresolved);
    return unresolved.isEmpty() ? Optional.of(resolved) : Optional.empty();
  }

  /**
   * Merges the default property sources with the provided extra sources.
   *
   * <p>The returned list will contain all the sources from {@code this.sources}
   * followed by the ones given in {@code extraSources}, if any. If {@code extraSources} is
   * {@code null} or empty, only the default sources will be included.
   *
   * @param extraSources additional property sources to merge with the defaults; may be {@code null}
   *                     or empty
   * @return a new list containing the merged property sources, never {@code null}
   */
  private List<PropertySource> mergeSources(PropertySource... extraSources) {
    List<PropertySource> merged = new ArrayList<>(this.sources);
    if (extraSources != null && extraSources.length > 0) {
      merged.addAll(List.of(extraSources));
    }
    return List.copyOf(merged);
  }

  /**
   * Recursively replaces all resolvable placeholders in the provided template.
   *
   * @param template         the string containing potential placeholders
   * @param keysInResolution a set of keys currently being resolved; used for circular reference
   *                         detection
   * @param unresolved       a set to collect keys that could not be resolved
   * @return the resulting string with all resolvable placeholders replaced
   */
  private String replace(String template, List<PropertySource> sources,
      Set<String> keysInResolution, Set<String> unresolved) {
    var result = new StringBuilder();
    var stack = new ArrayDeque<Integer>();

    var i = 0;
    final var prefixAdvance = prefix.length();
    while (i < template.length()) {
      if (isEscapedPrefix(template, i)) {
        result.deleteCharAt(result.length() - 1);
        result.append(prefix);
        i += prefixAdvance;
      } else if (isStartOfPlaceholder(template, i)) {
        stack.push(result.length());
        result.append(prefix);
        i += prefixAdvance;
      } else if (isEndOfPlaceholder(template, i, stack)) {
        processPlaceholder(result, stack.pop(), sources, keysInResolution, unresolved);
        i += suffix.length();
      } else {
        result.append(template.charAt(i));
        i++;
      }
    }
    return result.toString();
  }

  /**
   * Checks if the placeholder prefix at the specified index is escaped.
   *
   * @param template the string being processed
   * @param index    the current index
   * @return {@code true} if the prefix is escaped; {@code false} otherwise
   */
  private boolean isEscapedPrefix(String template, int index) {
    return index > 0 && template.startsWith(prefix, index)
        && template.charAt(index - 1) == escapeChar;
  }

  /**
   * Checks if the current index marks the start of a placeholder.
   *
   * @param template the string being processed
   * @param index    the current index
   * @return {@code true} if the prefix starts here; {@code false} otherwise
   */
  private boolean isStartOfPlaceholder(String template, int index) {
    return template.startsWith(prefix, index);
  }

  /**
   * Checks if the current index marks the end of a placeholder.
   *
   * @param template the string being processed
   * @param index    the current index
   * @param stack    the stack of open placeholder positions
   * @return {@code true} if the suffix starts here and there is an open placeholder; {@code false}
   * otherwise
   */
  private boolean isEndOfPlaceholder(String template, int index, Deque<Integer> stack) {
    return template.startsWith(suffix, index) && !stack.isEmpty();
  }

  /**
   * Processes a detected placeholder by resolving it and replacing it in the result buffer.
   *
   * @param result           the current string builder
   * @param startIndex       the index where the placeholder starts in {@code result}
   * @param keysInResolution the set of keys currently being resolved
   * @param unresolved       the set of keys that could not be resolved
   */
  private void processPlaceholder(StringBuilder result, int startIndex,
      List<PropertySource> sources,
      Set<String> keysInResolution, Set<String> unresolved) {
    String placeholder = result.substring(startIndex + prefix.length());
    result.delete(startIndex, result.length());
    String resolved = resolvePlaceholder(placeholder, sources, keysInResolution, unresolved);
    result.append(resolved);
  }

  /**
   * Resolves a single placeholder, handling circular references and fallback values.
   *
   * @param placeholder      the raw placeholder content without prefix/suffix
   * @param keysInResolution the set of keys currently being resolved
   * @param unresolved       the set of keys that could not be resolved
   * @return the resolved value or the unresolved placeholder as-is
   * @throws IllegalStateException if a circular reference is detected
   */
  private String resolvePlaceholder(String placeholder, List<PropertySource> sources,
      Set<String> keysInResolution, Set<String> unresolved) {
    var colonIndex = placeholder.indexOf(delimiter);
    var key = colonIndex >= 0 ? placeholder.substring(0, colonIndex) : placeholder;
    Supplier<String> fallbackSupplier = getFallback(placeholder, colonIndex);

    if (!keysInResolution.add(key)) {
      throw new IllegalStateException("circular reference detected for key: " + key);
    }

    String resolved = resolveKey(key, fallbackSupplier, sources, keysInResolution, unresolved);
    keysInResolution.remove(key);
    return resolved;
  }

  private Supplier<String> getFallback(String placeholder, int colonIndex) {
    return colonIndex >= 0
        ? () -> placeholder.substring(colonIndex + delimiter.length())
        : () -> null;
  }

  /**
   * Attempts to resolve the given key using configured sources. If not found, uses the fallback
   * value if provided.
   *
   * @param key              the key to resolve
   * @param fallbackSupplier the fallback value if the key is not found
   * @param keysInResolution the set of keys currently being resolved
   * @param unresolved       the set of keys that could not be resolved
   * @return the resolved value, the fallback, or the original placeholder if unresolved
   */
  private String resolveKey(String key, Supplier<String> fallbackSupplier,
      List<PropertySource> sources,
      Set<String> keysInResolution,
      Set<String> unresolved) {
    String value = resolve(key, sources);
    if (value != null) {
      return replace(value, sources, keysInResolution, unresolved);
    }

    String fallback = fallbackSupplier.get();
    if (fallback != null) {
      return replace(fallback, sources, keysInResolution, unresolved);
    }

    unresolved.add(key);
    return prefix + key + suffix;
  }

  /**
   * Resolves a key by querying all configured sources in order.
   *
   * @param key the key to look up
   * @return the value if found; {@code null} otherwise
   */
  private String resolve(String key, List<PropertySource> sources) {
    if (isBlank(key)) {
      return null;
    }

    for (var source : sources) {
      String value = source.get(key);
      if (value != null) {
        return value;
      }
    }

    return null;
  }
}
