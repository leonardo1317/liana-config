package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JacksonMappersTest {

  @Mock
  ServiceLoader<JsonFactory> loader;

  @Mock
  Iterator<JsonFactory> iterator;

  @Mock
  JsonFactory factory;

  @Test
  @DisplayName("should return JSON ObjectMapper when JSON factory is present in ServiceLoader")
  void shouldReturnJsonMapperWhenJsonFactoryPresent() {
    when(loader.iterator()).thenReturn(iterator);
    when(iterator.hasNext()).thenReturn(true, false);
    when(iterator.next()).thenReturn(factory);
    when(factory.getFormatName()).thenReturn("JSON");

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {

      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class))
          .thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      ObjectMapper mapper = mappers.getJson();
      assertNotNull(mapper);
    }
  }

  @Test
  @DisplayName("should return YAML ObjectMapper when YAML factory is present in ServiceLoader")
  void shouldReturnYamlMapperWhenYamlFactoryPresent() {
    when(loader.iterator()).thenReturn(iterator);
    when(iterator.hasNext()).thenReturn(true, false);
    when(iterator.next()).thenReturn(factory);
    when(factory.getFormatName()).thenReturn("YAML");

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {

      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class))
          .thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      ObjectMapper mapper = mappers.getYaml();
      assertNotNull(mapper);
    }
  }

  @Test
  @DisplayName("should return XML ObjectMapper when XML factory is present in ServiceLoader")
  void shouldReturnXmlMapperWhenXmlFactoryPresent() {
    when(loader.iterator()).thenReturn(iterator);
    when(iterator.hasNext()).thenReturn(true, false);
    when(iterator.next()).thenReturn(factory);
    when(factory.getFormatName()).thenReturn("XML");

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {

      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class))
          .thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      ObjectMapper mapper = mappers.getXml();
      assertNotNull(mapper);
    }
  }

  @Test
  @DisplayName("should return Properties ObjectMapper when Properties factory is present in ServiceLoader")
  void shouldReturnPropertiesMapperWhenPropertiesFactoryPresent() {
    when(loader.iterator()).thenReturn(iterator);
    when(iterator.hasNext()).thenReturn(true, false);
    when(iterator.next()).thenReturn(factory);
    when(factory.getFormatName()).thenReturn("java_properties");

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {

      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class))
          .thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      ObjectMapper mapper = mappers.getProperties();
      assertNotNull(mapper);
    }
  }

  @Test
  @DisplayName("should throw IllegalStateException when JSON factory is missing")
  void shouldThrowWhenJsonFactoryMissing() {
    when(loader.iterator()).thenReturn(Collections.emptyIterator());

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {
      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class)).thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      IllegalStateException exception = assertThrows(
          IllegalStateException.class,
          mappers::getJson
      );
      assertEquals("no mapper available for format JSON", exception.getMessage());
    }
  }

  @Test
  @DisplayName("should throw IllegalStateException when YAML factory is missing")
  void shouldThrowWhenYamlFactoryMissing() {
    when(loader.iterator()).thenReturn(Collections.emptyIterator());

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {
      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class)).thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      IllegalStateException exception = assertThrows(
          IllegalStateException.class,
          mappers::getYaml
      );
      assertEquals("no mapper available for format YAML", exception.getMessage());
    }
  }

  @Test
  @DisplayName("should throw IllegalStateException when XML factory is missing")
  void shouldThrowWhenXmlFactoryMissing() {
    when(loader.iterator()).thenReturn(Collections.emptyIterator());

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {
      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class)).thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      IllegalStateException exception = assertThrows(
          IllegalStateException.class,
          mappers::getXml
      );
      assertEquals("no mapper available for format XML", exception.getMessage());
    }
  }

  @Test
  @DisplayName("should throw IllegalStateException when Properties factory is missing")
  void shouldThrowWhenPropertiesFactoryMissing() {
    when(loader.iterator()).thenReturn(Collections.emptyIterator());

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {
      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class)).thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      IllegalStateException exception = assertThrows(
          IllegalStateException.class,
          mappers::getProperties
      );
      assertEquals("no mapper available for format java_properties", exception.getMessage());
    }
  }

  @Test
  @DisplayName("should throw when ServiceLoader provides a null JsonFactory")
  void shouldThrowWhenJsonFactoryIsNull() {
    when(loader.iterator()).thenReturn(iterator);
    when(iterator.hasNext()).thenReturn(true, false);
    when(iterator.next()).thenReturn(null);

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {
      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class)).thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      IllegalStateException exception = assertThrows(
          IllegalStateException.class,
          mappers::getJson
      );
      assertEquals("no mapper available for format JSON", exception.getMessage());
    }
  }

  @Test
  @DisplayName("should throw when JsonFactory format name is null")
  void shouldThrowWhenFormatNameIsNull() {
    when(loader.iterator()).thenReturn(iterator);
    when(iterator.hasNext()).thenReturn(true, false);
    when(iterator.next()).thenReturn(factory);
    when(factory.getFormatName()).thenReturn(null);

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {
      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class)).thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      IllegalStateException exception = assertThrows(
          IllegalStateException.class,
          mappers::getJson
      );
      assertEquals("no mapper available for format JSON", exception.getMessage());
    }
  }

  @Test
  @DisplayName("should throw when JsonFactory format name does not match requested type")
  void shouldThrowWhenFormatNameDoesNotMatch() {
    when(loader.iterator()).thenReturn(iterator);
    when(iterator.hasNext()).thenReturn(true, false);
    when(iterator.next()).thenReturn(factory);
    when(factory.getFormatName()).thenReturn("YAML");

    try (var mockedStatic = mockStatic(ServiceLoader.class)) {
      mockedStatic.when(() -> ServiceLoader.load(JsonFactory.class)).thenReturn(loader);

      JacksonMappers mappers = JacksonMappers.create();

      IllegalStateException exception = assertThrows(
          IllegalStateException.class,
          mappers::getJson
      );
      assertEquals("no mapper available for format JSON", exception.getMessage());
    }
  }
}
