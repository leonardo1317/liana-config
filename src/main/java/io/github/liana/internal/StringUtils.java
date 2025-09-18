package io.github.liana.internal;

/**
 * Utility methods for common {@link String} operations.
 *
 * <p>This class provides null-safe methods for handling strings, including checks for blank
 * values, default substitutions, and case-insensitive comparisons.
 *
 * <p>All methods are {@code static}. This class cannot be instantiated.
 */
public final class StringUtils {

  private StringUtils() {
  }

  /**
   * Returns the given {@code value} if it is not blank; otherwise returns {@code defaultValue}.
   *
   * <p>A string is considered blank if it is {@code null}, empty, or consists only of whitespace.
   *
   * @param value        the string to check, may be {@code null}
   * @param defaultValue the value to return if {@code value} is blank
   * @return {@code value} if not blank, otherwise {@code defaultValue}
   */
  public static String defaultIfBlank(String value, String defaultValue) {
    return isBlank(value) ? defaultValue : value;
  }

  /**
   * Returns the given {@code value} if it is not blank.
   *
   * <p>If the string is blank, an {@link IllegalArgumentException} is thrown with the given
   * {@code message}.
   *
   * @param value   the string to check, may be {@code null}
   * @param message the exception message to use if {@code value} is blank
   * @return the given {@code value} if not blank
   * @throws IllegalArgumentException if {@code value} is blank
   */
  public static String requireNonBlank(String value, String message) {
    if (isBlank(value)) {
      throw new IllegalArgumentException(message);
    }

    return value;
  }

  /**
   * Returns {@code true} if the given {@code value} is {@code null}, empty, or contains only
   * whitespace characters.
   *
   * @param value the string to check, may be {@code null}
   * @return {@code true} if the string is blank, otherwise {@code false}
   */
  public static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  /**
   * Compares two strings case-insensitively, handling {@code null} values safely.
   *
   * @param first  the first string, may be {@code null}
   * @param second the second string, may be {@code null}
   * @return {@code true} if both strings are equal ignoring case, or both are {@code null}
   */
  public static boolean equalsIgnoreCase(String first, String second) {
    return first == null ? second == null : first.equalsIgnoreCase(second);
  }
}
