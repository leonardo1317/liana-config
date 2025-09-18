package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.exception.ConversionException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JacksonPathMapperTest {

  private JacksonPathMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new JacksonPathMapper(new ObjectMapper());
  }

  @Test
  @DisplayName("should get entire source as typed map")
  void shouldGetEntireSourceAsTypedMap() {
    Map<String, Object> source = Map.of("env", "dev");
    Type type = new TypeReference<Map<String, Object>>() {
    }.getType();
    Optional<Map<String, Object>> result = mapper.get(source, type);
    assertTrue(result.isPresent());
    assertEquals("dev", result.get().get("env"));
  }

  @Test
  @DisplayName("should return empty optional when source missing")
  void shouldReturnEmptyOptionalWhenSourceMissing() {
    Type type = new TypeReference<Map<String, Object>>() {
    }.getType();
    Map<String, Object> source = Map.of("name", "my-service");

    Optional<Map<String, Object>> result = mapper.get(source, "port", type);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should get nested value using path and type")
  void shouldGetNestedValueUsingPathAndType() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service"
        )
    );

    Optional<String> name = mapper.get(source, "app.name", String.class);
    assertTrue(name.isPresent());
    assertEquals("my-service", name.get());
  }

  @Test
  @DisplayName("should return empty optional when path missing")
  void shouldReturnEmptyOptionalWhenPathMissing() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service"
        )
    );

    Optional<String> missing = mapper.get(source, "app.unknown", String.class);
    assertTrue(missing.isEmpty());
  }

  @Test
  @DisplayName("should get list of values from array path")
  void shouldGetListOfValuesFromArrayPath() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service",
            "features", List.of("logging", "metrics")
        )
    );

    List<String> features = mapper.getList(source, "app.features", String.class);
    assertEquals(List.of("logging", "metrics"), features);
  }

  @Test
  @DisplayName("should return empty list when path not found or not array")
  void shouldReturnEmptyListWhenPathNotFoundOrNotArray() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service"
        )
    );

    assertTrue(mapper.getList(source, "app.unknown", String.class).isEmpty());
    assertTrue(mapper.getList(source, "app.name", String.class).isEmpty());
  }

  @Test
  @DisplayName("should get map from object path")
  void shouldGetMapFromObjectPath() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service",
            "port", 8080
        )
    );

    Map<String, Object> appMap = mapper.getMap(source, "app", Object.class);
    assertEquals("my-service", appMap.get("name"));
    assertEquals(8080, appMap.get("port"));
  }

  @Test
  @DisplayName("should return empty map when path not found or not object")
  void shouldReturnEmptyMapWhenPathNotFoundOrNotObject() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "features", List.of("logging", "metrics")
        )
    );

    assertTrue(mapper.getMap(source, "app.unknown", Object.class).isEmpty());
    assertTrue(mapper.getMap(source, "app.features", Object.class).isEmpty());
  }

  @Test
  @DisplayName("should check if path exists")
  void shouldCheckIfPathExists() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service"
        )
    );

    assertTrue(mapper.hasPath(source, "app.name"));
    assertFalse(mapper.hasPath(source, "app.unknown"));
  }

  @Test
  @DisplayName("should throw ConversionException for invalid conversion")
  void shouldThrowConversionExceptionForInvalidConversion() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "port", 8080
        )
    );

    assertThrows(ConversionException.class,
        () -> mapper.get(source, "app.port", UUID.class).orElseThrow());
  }

  @Test
  @DisplayName("should return element when using array index with get")
  void shouldReturnElementWhenUsingArrayIndexWithGet() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "features", List.of("logging", "metrics")
        )
    );

    Optional<String> element = mapper.get(source, "app.features[0]", String.class);
    assertTrue(element.isPresent());
    assertEquals("logging", element.get());
  }

  @Test
  @DisplayName("should return full list when path points to array")
  void shouldReturnFullListWhenPathPointsToArray() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "features", List.of("logging", "metrics")
        )
    );

    List<String> features = mapper.getList(source, "app.features", String.class);
    assertEquals(List.of("logging", "metrics"), features);
  }

  @Test
  @DisplayName("should return empty list when using array index with get list")
  void shouldReturnEmptyListWhenUsingArrayIndexWithGetList() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "features", List.of("logging", "metrics")
        )
    );

    List<String> features = mapper.getList(source, "app.features[0]", String.class);
    assertTrue(features.isEmpty());
  }

  @Nested
  @DisplayName("Null handling")
  class NullHandlingTests {

    @Nested
    @DisplayName("when source is null")
    class SourceNullTests {

      @Test
      @DisplayName("should throw NullPointerException when getting by type")
      void shouldThrowWhenGettingByType() {
        assertThrows(NullPointerException.class, () -> mapper.get(null, String.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting by path")
      void shouldThrowWhenGettingByPath() {
        assertThrows(NullPointerException.class, () -> mapper.get(null, "app.name", String.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting list")
      void shouldThrowWhenGettingList() {
        assertThrows(NullPointerException.class,
            () -> mapper.getList(null, "app.features", String.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting map")
      void shouldThrowWhenGettingMap() {
        assertThrows(NullPointerException.class, () -> mapper.getMap(null, "app", Object.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when checking path existence")
      void shouldThrowWhenCheckingPath() {
        assertThrows(NullPointerException.class, () -> mapper.hasPath(null, "app.name"));
      }
    }

    @Nested
    @DisplayName("when path is null")
    class PathNullTests {

      private final Map<String, Object> source = Map.of("app", Map.of("name", "my-service"));

      @Test
      @DisplayName("should throw NullPointerException when getting")
      void shouldThrowWhenGetting() {
        assertThrows(NullPointerException.class, () -> mapper.get(source, null, String.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting list")
      void shouldThrowWhenGettingList() {
        assertThrows(NullPointerException.class, () -> mapper.getList(source, null, String.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting map")
      void shouldThrowWhenGettingMap() {
        assertThrows(NullPointerException.class, () -> mapper.getMap(source, null, Object.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when checking path existence")
      void shouldThrowWhenCheckingPath() {
        assertThrows(NullPointerException.class, () -> mapper.hasPath(source, null));
      }
    }

    @Nested
    @DisplayName("when targetType is null")
    class TargetTypeNullTests {

      private final Map<String, Object> source = Map.of(
          "app", Map.of("name", "my-service", "features", List.of("logging", "metrics"))
      );

      @Test
      @DisplayName("should throw NullPointerException when getting by type")
      void shouldThrowWhenGettingByType() {
        assertThrows(NullPointerException.class, () -> mapper.get(source, null));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting by path")
      void shouldThrowWhenGettingByPath() {
        assertThrows(NullPointerException.class, () -> mapper.get(source, "app.name", null));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting list")
      void shouldThrowWhenGettingList() {
        assertThrows(NullPointerException.class,
            () -> mapper.getList(source, "app.features", null));
      }
    }
  }
}
