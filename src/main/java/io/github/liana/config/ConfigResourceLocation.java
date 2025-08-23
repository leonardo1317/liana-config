package io.github.liana.config;

import io.github.liana.internal.ImmutableConfigMap;
import io.github.liana.internal.ImmutableConfigSet;

public interface ConfigResourceLocation {

  String getProvider();

  ImmutableConfigSet getResourceNames();

  ImmutableConfigMap getVariables();

  boolean isVerboseLogging();

  Placeholder getPlaceholder();
}
