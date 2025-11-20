package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * A generic, thread-safe registry that lazily discovers and caches singleton instances of type
 * {@code R} from service implementations of type {@code T} using a {@link ServiceLoader}.
 *
 * <p>This class is designed to support flexible service resolution by:
 * <ul>
 *   <li>Loading implementations of {@code T} dynamically using {@link ServiceLoader}.</li>
 *   <li>Filtering services based on a {@link BiPredicate} that determines whether a service
 *       matches a requested type alias.</li>
 *   <li>Converting the selected service into an instance of {@code R} via a {@link Function}.</li>
 *   <li>Caching created instances to ensure singleton-like behavior for each type alias.</li>
 * </ul>
 *
 * <p>Instances are cached using the lowercase form of the requested type alias, ensuring
 * case-insensitive lookups while avoiding redundant object creation.
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe thanks to the use of a {@link ConcurrentHashMap} for caching.</p>
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * ServiceRegistry<JsonFactory, ObjectMapper> registry =
 *     new ServiceRegistry<>(
 *         ServiceLoader.load(JsonFactory.class),
 *         (factory, type) -> factory.getFormatName().equalsIgnoreCase(type),
 *         ObjectMapper::new
 *     );
 *
 * ObjectMapper jsonMapper = registry.get("json")
 *     .orElseThrow(() -> new IllegalStateException("No JSON mapper available"));
 * }</pre>
 *
 * @param <T> the type of service loaded by {@link ServiceLoader}
 * @param <R> the type of object produced from the service
 */
public class ServiceRegistry<T, R> {

  private final ServiceLoader<T> loader;
  private final Map<String, R> cache = new ConcurrentHashMap<>();
  private final BiPredicate<T, String> filter;
  private final Function<T, R> function;

  /**
   * Creates a new {@code ServiceRegistry} with the given loader, filter, and factory.
   *
   * @param loader   the {@link ServiceLoader} used to discover service implementations; must not be
   *                 {@code null}
   * @param filter   a {@link BiPredicate} that returns {@code true} if a service matches the
   *                 requested type alias; must not be {@code null}
   * @param function a {@link Function} that converts a matching service of type {@code T} into the
   *                 mapped type {@code R}; must not be {@code null}
   * @throws NullPointerException if any parameter is {@code null}
   */
  public ServiceRegistry(ServiceLoader<T> loader,
      BiPredicate<T, String> filter,
      Function<T, R> function) {
    this.loader = requireNonNull(loader, "loader must not be null");
    this.filter = requireNonNull(filter, "filter must not be null");
    this.function = requireNonNull(function, "function must not be null");
  }

  /**
   * Returns a cached singleton instance of {@code R} for the given type alias.
   *
   * <p>If an instance for the requested type alias already exists in the cache, it is returned.
   * Otherwise:
   * <ol>
   *   <li>The registry searches all services provided by the {@link ServiceLoader}.</li>
   *   <li>Each service is tested with the {@link BiPredicate} filter.</li>
   *   <li>The first matching service is converted into an {@code R} using the factory function.</li>
   *   <li>The result is cached and returned.</li>
   * </ol>
   *
   * @param type the type alias to look up; must not be {@code null}
   * @return an {@link Optional} containing the singleton instance of {@code R}, or empty if no
   * matching service is found
   * @throws NullPointerException if {@code type} is {@code null}
   */
  public Optional<R> get(String type) {
    requireNonNull(type, "type must not be null");
    return Optional.ofNullable(cache.computeIfAbsent(type.toLowerCase(), this::create));
  }

  /**
   * Creates an instance of {@code R} for the given type alias.
   *
   * <p>The method iterates through all services provided by the {@link ServiceLoader}, applies
   * the filter, and if a match is found, transforms the service into an {@code R} using the factory
   * function.
   *
   * <p>If no matching service is found, this method returns {@code null}, which is later wrapped
   * in an {@link Optional} by {@link #get(String)}.
   *
   * @param type the type alias to look up
   * @return the created instance of {@code R}, or {@code null} if no matching service is found
   */
  private R create(String type) {
    for (T service : loader) {
      if (filter.test(service, type)) {
        return function.apply(service);
      }
    }
    return null;
  }
}
