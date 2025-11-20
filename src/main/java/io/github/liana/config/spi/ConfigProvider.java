package io.github.liana.config.spi;

import static io.github.liana.config.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.core.ConfigResource;
import io.github.liana.config.core.ConfigResourceReference;
import io.github.liana.config.core.exception.ConfigProviderException;
import java.util.Set;

/**
 * Provides configuration resources from different sources. Implementations handle specific
 * locations like filesystem, classpath or remote URLs.
 */
public interface ConfigProvider extends Strategy<String> {

  /**
   * Unique identifier for this provider (e.g., "filesystem", "classpath").
   */
  @Override
  Set<String> getKeys();

  /**
   * Resolves a configuration resource into a loadable format.
   *
   * @throws ConfigProviderException if resource can't be resolved
   * @throws NullPointerException    if resource is null
   */
  ConfigResource resolveResource(ConfigResourceReference resource);

  /**
   * Validates basic resource requirements. Default checks: non-null resource and resource name.
   */
  default void validateResource(ConfigResourceReference resource) {
    requireNonNull(resource, "ConfigResourceReference must not be null");
    requireNonBlank(resource.resourceName(), "ResourceNames must not be null");
  }
}
