package io.github.liana.core.configs;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class FileExtensionValidator {
    private static final Set<String> DEFAULT_EXTENSIONS = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList("properties", "yaml", "yml", "json"))
    );

    private FileExtensionValidator() {
    }

    public static boolean isValid(Set<String> allowedExtensions, String fileExtension) {
        if (allowedExtensions == null || allowedExtensions.isEmpty() || fileExtension == null || fileExtension.isBlank()) {
            return false;
        }


        Set<String> extensions = allowedExtensions.stream()
                .filter(ext -> ext != null && !ext.isBlank())
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return extensions.contains(fileExtension.trim().toLowerCase());
    }

    public static Set<String> defaultExtensions() {
        return DEFAULT_EXTENSIONS;
    }

}
