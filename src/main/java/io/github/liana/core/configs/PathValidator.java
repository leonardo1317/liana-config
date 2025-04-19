package io.github.liana.core.configs;

final class PathValidator {

    public PathValidator(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name path is null or empty.");
        }

        if (!resourceExists(fileName)) {
            throw new IllegalArgumentException("Resource not found: " + fileName);
        }
    }

    public static boolean resourceExists(String fileName) {
        return PathValidator.class.getClassLoader().getResource(fileName) != null;
    }

}
