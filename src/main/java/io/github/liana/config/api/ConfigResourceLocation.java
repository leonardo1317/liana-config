package io.github.liana.config.api;

import io.github.liana.config.internal.ImmutableConfigMap;
import io.github.liana.config.internal.ImmutableConfigSet;

public interface ConfigResourceLocation {

  String getProvider();

  ImmutableConfigSet getBaseDirectories();

  ImmutableConfigSet getResourceNames();

  ImmutableConfigMap getVariables();

  boolean isVerboseLogging();

  Placeholder getPlaceholder();
}
