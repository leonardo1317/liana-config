package io.github.liana.config.api;

import io.github.liana.config.spi.ConfigLoader;
import io.github.liana.config.spi.ConfigProvider;

public interface LianaConfigBuilder {

  LianaConfigBuilder addProviders(ConfigProvider... providers);

  LianaConfigBuilder addLoaders(ConfigLoader... loaders);

  ConfigManager build();
}
