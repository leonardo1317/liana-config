package io.github.liana.core.configs;

import static java.util.Objects.requireNonNull;

public interface ConfigProvider {
    String getProvider();

    ConfigResource resolveResource(ConfigSourceLocator locator);

    default void validateSource(ConfigSourceLocator locator) {
        requireNonNull(locator, "ConfigSourceLocator must not be null");
        String filename = requireNonNull(locator.getFilename(), "filename must not be null");
        if (filename.isBlank()) {
            throw new IllegalArgumentException("Filename must not be blank");
        }
    }
}
