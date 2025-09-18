package io.github.liana.config;

import static io.github.liana.internal.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigLoaderException;
import io.github.liana.config.exception.ConfigProviderException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

class DefaultConfigManager implements ConfigManager {

  private static final KeyNormalizer<String> keyNormalizer = key -> key.toLowerCase(Locale.ROOT);
  private static final long NANOS_PER_MILLISECOND = 1_000_000L;
  private static final ConfigMapCache<String, Object> cache = new ConfigMapCache<>();
  private static final JacksonMappers jacksonMappers = JacksonMappers.create();
  private static final StrategyRegistry<String, ConfigLoader> configLoaderRegistry = new StrategyRegistry<>(
      keyNormalizer,
      new PropertiesLoader(new JacksonParser(jacksonMappers.getProperties())),
      new YamlLoader(new JacksonParser(jacksonMappers.getYaml())),
      new JsonLoader(new JacksonParser(jacksonMappers.getJson())),
      new XmlLoader(new JacksonParser(jacksonMappers.getXml())));

  private static final StrategyRegistry<String, ConfigProvider> configProviderRegistry = new StrategyRegistry<>(
      keyNormalizer,
      new ClasspathProvider());

  private static final ConfigResourceLoader CONFIG_RESOURCE_LOADER = ConfigResourceLoader.of(
      configLoaderRegistry);

  private static final ConfigResourceProvider configResourceProvider = ConfigResourceProvider.of(
      configProviderRegistry
  );

  private static final JacksonMerger MERGER = new JacksonMerger(jacksonMappers.getJson());
  private static final JacksonInterpolator INTERPOLATOR = new JacksonInterpolator(
      jacksonMappers.getJson());

  @Override
  public ConfigReader load(ConfigResourceLocation location) {
    requireNonNull(location, "ConfigResourceLocation cannot be null when loading configuration");
    Map<String, Object> cachedConfig = cache.get(() -> getConfig(location));

    return new DefaultConfigReader(CONFIG_RESOURCE_LOADER.loadFromMap(cachedConfig));
  }

  private Map<String, Object> getConfig(ConfigResourceLocation location) {
    ConfigLogger log = ConsoleConfigLogger.getLogger(location.isVerboseLogging());
    log.debug(() -> "Starting configuration load");
    ConfigResourcePreparer configResourcePreparer = new ConfigResourcePreparer(location);
    List<ConfigResourceReference> references = configResourcePreparer.prepare();
    List<Map<String, Object>> configs = processConfigResources(references, log);

    int total = references.size();
    int loaded = configs.size();
    int failed = total - loaded;
    log.info(() -> String.format("Configuration load completed: loaded=%d, failed=%d (total=%d)",
        loaded, failed, total));

    return INTERPOLATOR.interpolate(MERGER.merge(configs), location.getPlaceholder(),
        location.getVariables().toMap());
  }

  private List<Map<String, Object>> processConfigResources(List<ConfigResourceReference> references,
      ConfigLogger log) {
    List<Map<String, Object>> configs = new ArrayList<>(references.size());
    for (ConfigResourceReference reference : references) {
      if (isBlank(reference.provider()) || isBlank(reference.resourceName())) {
        log.debug(() -> "Empty provider or resource name");
        continue;
      }

      processSingleConfigResource(reference, log).ifPresent(configs::add);
    }

    return Collections.unmodifiableList(configs);
  }

  private Optional<Map<String, Object>> processSingleConfigResource(
      ConfigResourceReference reference, ConfigLogger log) {
    log.debug(() -> "Loading resource: " + reference.resourceName());
    try {
      long startTime = System.nanoTime();
      ConfigResource configResource = configResourceProvider.resolve(reference);
      Configuration configuration = CONFIG_RESOURCE_LOADER.loadFromResource(configResource);
      Map<String, Object> allConfig = configuration.getAllConfig();
      long durationMs = (System.nanoTime() - startTime) / NANOS_PER_MILLISECOND;
      log.debug(() -> String.format("Loaded %s with %d entries in %dms",
          reference.resourceName(), allConfig.size(), durationMs));

      return Optional.of(allConfig);
    } catch (ConfigProviderException e) {
      log.error(() -> "Failed to obtain provider for " + reference.provider(), e);
      return Optional.empty();
    } catch (ConfigLoaderException e) {
      log.error(() -> "Failed to load configuration from " + reference.resourceName(), e);
      return Optional.empty();
    } catch (Exception e) {
      log.error(() -> "Unexpected error while processing resource " + reference.resourceName(),
          e);
      return Optional.empty();
    }
  }
}
