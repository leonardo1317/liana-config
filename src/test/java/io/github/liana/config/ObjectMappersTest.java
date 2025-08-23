package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ObjectMappersTest {

  @Test
  @DisplayName("should return same JSON ObjectMapper instance")
  void shouldReturnSameJsonInstance() {
    ObjectMapper instance1 = ObjectMappers.getJsonInstance();
    ObjectMapper instance2 = ObjectMappers.getJsonInstance();

    assertNotNull(instance1);
    assertSame(instance1, instance2);
  }

  @Test
  @DisplayName("should return same YAML ObjectMapper instance")
  void shouldReturnSameYamlInstance() {
    ObjectMapper instance1 = ObjectMappers.getYamlInstance();
    ObjectMapper instance2 = ObjectMappers.getYamlInstance();

    assertNotNull(instance1);
    assertSame(instance1, instance2);
  }

  @Test
  @DisplayName("should return same XML ObjectMapper instance")
  void shouldReturnSameXmlInstance() {
    ObjectMapper instance1 = ObjectMappers.getXmlInstance();
    ObjectMapper instance2 = ObjectMappers.getXmlInstance();

    assertNotNull(instance1);
    assertSame(instance1, instance2);
  }

  @Test
  @DisplayName("should return same Properties ObjectMapper instance")
  void shouldReturnSamePropertiesInstance() {
    ObjectMapper instance1 = ObjectMappers.getPropertiesInstance();
    ObjectMapper instance2 = ObjectMappers.getPropertiesInstance();

    assertNotNull(instance1);
    assertSame(instance1, instance2);
  }
}
