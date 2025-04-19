package io.github.liana.core.configs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class ConfigManager {
    private static final ObjectMapper mapper = new ObjectMapper();

    private ConfigManager() {
    }

    public static ConfigReader defaultConfig() {
        return fromFile(DefaultConfigSource.defaultConfig());
    }

    public static ConfigReader fromFile(ConfigSource configSource) {
        List<Map<String, Object>> configs = configSource.getConfigFileSources().stream()
                .map(ConfigProviderFactory::resolveResource)
                .map(ConfigLoaderFactory::fromFile)
                .map(ConfigWrapper::getAllSettings)
                .toList();

        Map<String, Object> mergedConfig = mergeInConfig(configs);
        return new DefaultConfigReader(ConfigLoaderFactory.fromMap(mergedConfig));
    }

    private static Map<String, Object> mergeInConfig(List<Map<String, Object>> configs) {
        ObjectNode merged = mapper.createObjectNode();
        for (Map<String, Object> config : configs) {
            ObjectNode current = mapper.convertValue(config, ObjectNode.class);
            try {
                mapper.readerForUpdating(merged).readValue(current);
            } catch (IOException ex) {
                throw new RuntimeException("Error merging configurations", ex);
            }
        }

        return mapper.convertValue(merged, new TypeReference<>() {
        });
    }
}
