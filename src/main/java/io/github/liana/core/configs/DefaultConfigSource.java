package io.github.liana.core.configs;

import java.util.Map;

import static io.github.liana.core.configs.FileExtensionValidator.defaultExtensions;
import static io.github.liana.core.configs.PathValidator.resourceExists;

public class DefaultConfigSource {
    private static final String DEFAULT_FILE_NAME = "application";
    private static final String DEFAULT_FILE_NAME_PATTERN = DEFAULT_FILE_NAME + "-" + "${profile}";
    private static final String LIANA_PROFILE_ENV_VAR = "LIANA_PROFILE";

    public DefaultConfigSource() {
    }

    public static ConfigSource defaultConfig() {
        return defaultExtensions().stream()
                .filter(extension -> resourceExists(DEFAULT_FILE_NAME + "." + extension))
                .findFirst()
                .map(DefaultConfigSource::buildConfigSource)
                .orElseThrow(() -> new IllegalStateException("No standard config file found in classpath"));
    }

    private static ConfigSource buildConfigSource(String extension) {
        return ConfigSource.builder()
                .baseFilename(DEFAULT_FILE_NAME + "." + extension)
                .filenamePattern(DEFAULT_FILE_NAME_PATTERN + "." + extension)
                .variables(Map.of("profile", getProfile()))
                .build();
    }

    private static String getProfile() {
        return System.getenv().getOrDefault(LIANA_PROFILE_ENV_VAR, "default");
    }
}
