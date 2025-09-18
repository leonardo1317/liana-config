package io.github.liana.config;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Supported configuration file formats and their associated extensions.
 */
public enum ConfigFileFormat {
  PROPERTIES(of("properties")),
  YAML(of("yaml", "yml")),
  JSON(of("json")),
  XML(of("xml"));

  private final Set<String> extensions;
  private static final Set<String> ALL_EXTENSIONS;

  static {
    Set<String> tempExtensions = new LinkedHashSet<>();
    for (ConfigFileFormat fileFormat : values()) {
      for (String extension : fileFormat.extensions) {
        if (!tempExtensions.add(extension.toLowerCase(Locale.ROOT))) {
          throw new IllegalStateException("duplicate extension: " + extension);
        }
      }
    }

    ALL_EXTENSIONS = Collections.unmodifiableSet(tempExtensions);
  }

  ConfigFileFormat(Set<String> extensions) {
    this.extensions = extensions;
  }

  /**
   * Gets all valid extensions for this format.
   *
   * @return Immutable set of extensions in lowercase
   */
  public Set<String> getExtensions() {
    return extensions;
  }

  /**
   * Gets all supported extensions across all formats (no duplicates).
   *
   * @return Immutable set in declaration order
   */
  public static Set<String> getAllSupportedExtensions() {
    return ALL_EXTENSIONS;
  }

  private static Set<String> of(String... values) {
    return Collections.unmodifiableSet(new LinkedHashSet<>(List.of(values)));
  }
}
