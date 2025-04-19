package io.github.liana.core.configs;

import java.util.Set;

public interface ConfigLoader {
    Set<String> getExtensions();

    ConfigWrapper load(ConfigResource resource);
}
