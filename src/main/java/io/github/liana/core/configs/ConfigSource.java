package io.github.liana.core.configs;

import org.apache.commons.text.StringSubstitutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigSource {
    private final List<ConfigSourceLocator> configSourceLocators;

    public ConfigSource(List<ConfigSourceLocator> configSourceLocators) {
        this.configSourceLocators = configSourceLocators;
    }

    public List<ConfigSourceLocator> getConfigFileSources() {
        return configSourceLocators;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "ConfigSource{" +
                "configSourceLocators=" + configSourceLocators +
                '}';
    }

    public static class Builder {
        private String baseFilename;
        private String filenamePattern;
        private Map<String, String> variables;
        private String provider;
        private Map<String, String> credentials;

        public Builder baseFilename(String baseFilename) {
            this.baseFilename = baseFilename;
            return this;
        }

        public Builder filenamePattern(String filenamePattern) {
            this.filenamePattern = filenamePattern;
            return this;
        }

        public Builder variables(Map<String, String> variables) {
            this.variables = variables;
            return this;
        }

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder credentials(Map<String, String> credentials) {
            this.credentials = credentials;
            return this;
        }

        public ConfigSource build() {
            if (provider == null || provider.isBlank()) {
                provider = "classpath";
            }

            List<ConfigSourceLocator> configSourceLocators = new ArrayList<>();
            configSourceLocators.add(new ConfigSourceLocator(baseFilename, provider, credentials));

            if (filenamePattern != null && !filenamePattern.isBlank() && variables != null && !variables.isEmpty()) {
                configSourceLocators.add(new ConfigSourceLocator(StringSubstitutor.replace(filenamePattern, variables), provider, credentials));
            }

            return new ConfigSource(configSourceLocators);
        }
    }
}
