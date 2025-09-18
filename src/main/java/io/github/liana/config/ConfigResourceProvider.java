package io.github.liana.config;

import static io.github.liana.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigProviderException;

/**
 * Resolves {@link ConfigResource} instances from different types of providers.
 *
 * <p>This class delegates the resolution process to a set of registered
 * {@link ConfigProvider} strategies, based on the provider identifier declared in the
 * {@link ConfigResourceReference}. If no matching provider is found, a
 * {@link ConfigProviderException} is thrown.</p>
 *
 * <p>Instances of this class are immutable and thread-safe.</p>
 */
class ConfigResourceProvider {

  private final StrategyRegistry<String, ConfigProvider> strategies;

  /**
   * Creates a new {@code ConfigResourceProvider} with the given strategy registry.
   *
   * @param strategies the registry containing {@link ConfigProvider} implementations keyed by
   *                   provider name, must not be {@code null}
   */
  private ConfigResourceProvider(StrategyRegistry<String, ConfigProvider> strategies) {
    this.strategies = requireNonNull(strategies, "strategies must not be null");
  }

  /**
   * Factory method to create a {@code ConfigResourceProvider}.
   *
   * @param strategies the registry containing {@link ConfigProvider} implementations keyed by
   *                   provider name, must not be {@code null}
   * @return a new {@code ConfigResourceProvider} instance
   */
  public static ConfigResourceProvider of(StrategyRegistry<String, ConfigProvider> strategies) {
    return new ConfigResourceProvider(strategies);
  }

  /**
   * Resolves a {@link ConfigResource} from a {@link ConfigResourceReference}.
   *
   * <p>The provider name contained in the reference determines which
   * {@link ConfigProvider} will be used. If no matching strategy is found, a
   * {@link ConfigProviderException} is thrown.</p>
   *
   * @param resource the configuration resource reference to resolve, must not be {@code null}
   * @return the resolved {@link ConfigResource}
   * @throws ConfigProviderException if no matching provider exists or if an error occurs
   */
  public ConfigResource resolve(ConfigResourceReference resource) {
    requireNonNull(resource, "configResourceReference cannot be null to create a configResource");
    String provider = requireNonBlank(resource.provider(),
        "provider cannot be null or blank to create a configResource");

    return strategies.get(provider)
        .map(configProvider -> resolve(configProvider, resource))
        .orElseThrow(
            () -> new ConfigProviderException(
                "no config provider found for provider " + provider));
  }

  /**
   * Executes the given {@link ConfigProvider} to resolve a configuration resource.
   *
   * @param configProvider the provider to use, must not be {@code null}
   * @param resource       the configuration resource reference, must not be {@code null}
   * @return the resolved {@link ConfigResource}
   * @throws ConfigProviderException if the provider fails or an unexpected error occurs
   */
  private ConfigResource resolve(ConfigProvider configProvider,
      ConfigResourceReference resource) {
    try {
      return configProvider.resolveResource(resource);
    } catch (ConfigProviderException e) {
      throw e;
    } catch (Exception e) {
      throw new ConfigProviderException(
          "unexpected error while obtaining provider for resource " + configProvider.getClass()
              .getSimpleName(),
          e);
    }
  }
}
