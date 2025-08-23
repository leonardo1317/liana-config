package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import io.github.liana.config.exception.ConfigLoaderException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertiesConfigLoaderTest {

  @Mock
  private ConfigResource resource;
  private ConfigLoader loader;

  @BeforeEach
  void setUp() {
    loader = new PropertiesConfigLoader();
  }

  @Test
  @DisplayName("should return Properties as supported file format")
  void shouldReturnPropertiesAsSupportedFileFormat() {
    assertEquals(ConfigFileFormat.PROPERTIES, loader.getFileFormat());
  }

  @Test
  @DisplayName("should load valid Properties configuration successfully")
  void shouldLoadValidPropertiesConfigurationSuccessfully() {
    String content = "key=value";
    InputStream input = new ByteArrayInputStream(content.getBytes());

    when(resource.getInputStream()).thenReturn(input);
    when(resource.getResourceName()).thenReturn("test.properties");

    Configuration config = loader.load(resource);

    assertNotNull(config);
    assertEquals(Optional.of("value"), config.get("key", String.class));
  }

  @Test
  @DisplayName("should throw NullPointerException when resource is null")
  void shouldThrowNullPointerExceptionWhenResourceIsNull() {
    assertThrows(NullPointerException.class, () -> loader.load(null));
  }

  @Test
  @DisplayName("should throw NullPointerException when input stream is null")
  void shouldThrowNullPointerExceptionWhenInputStreamIsNull() {
    when(resource.getInputStream()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> loader.load(resource));
  }

  @Test
  @DisplayName("should throw NullPointerException when resource name is null")
  void shouldThrowNullPointerExceptionWhenResourceNameIsNull() {
    String content = "key=value";
    InputStream input = new ByteArrayInputStream(content.getBytes());

    when(resource.getInputStream()).thenReturn(input);
    when(resource.getResourceName()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> loader.load(resource));
  }

  @Test
  @DisplayName("should throw ConfigLoaderException when Properties is malformed")
  void shouldThrowConfigLoaderExceptionWhenPropertiesIsMalformed() {
    String content = "key\\u12=value";
    InputStream input = new ByteArrayInputStream(content.getBytes());

    when(resource.getInputStream()).thenReturn(input);
    when(resource.getResourceName()).thenReturn("malformed.properties");

    assertThrows(ConfigLoaderException.class, () -> loader.load(resource));
  }
}
