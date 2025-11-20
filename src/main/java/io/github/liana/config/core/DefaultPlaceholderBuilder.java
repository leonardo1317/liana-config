package io.github.liana.config.core;

import io.github.liana.config.api.Placeholder;
import io.github.liana.config.api.PlaceholderBuilder;

/**
 * A builder for creating {@link DefaultPlaceholder} instances with custom configuration.
 *
 * <p>By default, the builder creates placeholders with:
 * <ul>
 *   <li>Prefix: {@code "${"}</li>
 *   <li>Suffix: {@code "}"}</li>
 *   <li>Delimiter: {@code ":"}</li>
 *   <li>Escape character: {@code '\\'}</li>
 *   <li>Sources: environment variables via {@link PropertySources#fromEnv()} ()}</li>
 * </ul>
 *
 * <p>Additional customization methods allow changing the prefix, suffix, delimiter, escape
 * character, and placeholder sources.
 *
 * <p>This builder is not thread-safe and should be used in a single-threaded context or with
 * external synchronization.
 */
public final class DefaultPlaceholderBuilder implements PlaceholderBuilder {

  private String prefix = "${";
  private String suffix = "}";
  private String delimiter = ":";
  private char escapeChar = '\\';

  /**
   * Sets the placeholder prefix.
   *
   * @param prefix the prefix string to identify placeholders; must not be null
   * @return this builder instance
   */
  @Override
  public PlaceholderBuilder prefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  /**
   * Sets the placeholder suffix.
   *
   * @param suffix the suffix string to identify placeholders; must not be null
   * @return this builder instance
   */
  @Override
  public PlaceholderBuilder suffix(String suffix) {
    this.suffix = suffix;
    return this;
  }

  /**
   * Sets the delimiter used to separate the placeholder key from its default value.
   *
   * @param delimiter the delimiter string; must not be null
   * @return this builder instance
   */
  @Override
  public PlaceholderBuilder delimiter(String delimiter) {
    this.delimiter = delimiter;
    return this;
  }

  /**
   * Sets the escape character used to prevent placeholder resolution.
   *
   * @param escapeChar the escape character
   * @return this builder instance
   */
  @Override
  public PlaceholderBuilder escapeChar(char escapeChar) {
    this.escapeChar = escapeChar;
    return this;
  }

  /**
   * Builds a new {@link DefaultPlaceholder} instance with the current configuration.
   *
   * @return a new configured {@link DefaultPlaceholder}
   */
  @Override
  public Placeholder build() {
    return new DefaultPlaceholder(prefix, suffix, delimiter, escapeChar);
  }
}
