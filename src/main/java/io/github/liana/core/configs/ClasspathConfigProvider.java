package io.github.liana.core.configs;

import java.io.InputStream;

public class ClasspathConfigProvider implements ConfigProvider {

    @Override
    public String getProvider() {
        return "classpath";
    }

    @Override
    public ConfigResource resolveResource(ConfigSourceLocator locator) {
        validateSource(locator);
        InputStream input = getClass().getClassLoader().getResourceAsStream(locator.getFilename());
        if (input == null) {
            throw new RuntimeException("Config resource not found: " + locator.getFilename());
        }
        return new ConfigResource(locator.getFilename(), input);
    }
}
