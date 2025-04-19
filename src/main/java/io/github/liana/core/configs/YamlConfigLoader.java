package io.github.liana.core.configs;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

class YamlConfigLoader implements ConfigLoader {

    @Override
    public Set<String> getExtensions() {
        return Set.of("yaml", "yml");
    }

    @Override
    public ConfigWrapper load(ConfigResource resource) {
        try (InputStream input = resource.getInputStream()) {
            YAMLConfiguration config = new YAMLConfiguration();
            config.read(input);
            return new ConfigWrapper(config);
        } catch (ConfigurationException | IOException ex) {
            throw new RuntimeException("Error loading Yaml config from " + resource.getFilename(), ex);
        }
    }
}
