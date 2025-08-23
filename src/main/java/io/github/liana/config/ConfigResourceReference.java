package io.github.liana.config;

public class ConfigResourceReference {

  private final String provider;
  private final String resourceName;

  public ConfigResourceReference(String provider, String resourceName) {
    this.provider = provider;
    this.resourceName = resourceName;
  }

  public String getProvider() {
    return provider;
  }

  public String getResourceName() {
    return resourceName;
  }

  @Override
  public String toString() {
    return "ConfigResourceReference{" +
        "provider='" + provider + '\'' +
        ", resourceName='" + resourceName + '\'' +
        '}';
  }
}
