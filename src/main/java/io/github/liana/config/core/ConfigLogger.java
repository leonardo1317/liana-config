package io.github.liana.config.core;

import java.util.function.Supplier;

/**
 * A logger interface for configuration-related messages.
 *
 * <p>Allows logging messages at different levels (info, debug, warn, error),
 * using {@link Supplier} to defer message construction until logging is needed.
 */
public interface ConfigLogger {

  /**
   * Logs an informational message.
   *
   * <p>The message is provided lazily using a {@link Supplier}.
   *
   * @param message the message supplier
   */
  void info(Supplier<String> message);

  /**
   * Logs a debug-level message.
   *
   * <p>The message is provided lazily using a {@link Supplier}.
   *
   * @param message the message supplier
   */
  void debug(Supplier<String> message);

  /**
   * Logs a warning-level message.
   *
   * <p>The message is provided lazily using a {@link Supplier}.
   *
   * @param message the message supplier
   */
  void warn(Supplier<String> message);

  /**
   * Logs an error-level message along with an exception.
   *
   * <p>The message is provided lazily using a {@link Supplier}.
   *
   * @param message the message supplier
   * @param e       the exception to log
   */
  void error(Supplier<String> message, Exception e);
}
