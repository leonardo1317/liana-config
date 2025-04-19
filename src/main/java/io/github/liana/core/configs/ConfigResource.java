package io.github.liana.core.configs;

import java.io.InputStream;

public class ConfigResource {
    private final String filename;
    private final InputStream inputStream;

    public ConfigResource(String filename, InputStream inputStream) {
        this.filename = filename;
        this.inputStream = inputStream;
    }

    public String getFilename() {
        return filename;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String toString() {
        return "ConfigResource{" +
                "filename='" + filename + '\'' +
                ", inputStream=" + inputStream +
                '}';
    }
}
