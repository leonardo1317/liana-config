package io.github.liana.core.configs;

import java.util.Map;

public class ConfigSourceLocator {
    private final String filename;
    private final String provider;
    private final Map<String, String> credentials;

    public ConfigSourceLocator(String filename, String provider, Map<String, String> credentials) {
        this.filename = filename;
        this.provider = provider;
        this.credentials = credentials;
    }

    public String getFilename() {
        return filename;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public String getProvider() {
        return provider;
    }
}
