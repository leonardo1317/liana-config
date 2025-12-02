package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.liana.config.core.exception.ConversionException;
import io.github.liana.config.core.exception.MergeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JacksonMergerTest {

  private JacksonMerger merger;

  @BeforeEach
  void setUp() {
    merger = new JacksonMerger(new ObjectMapper());
  }

  @Test
  @DisplayName("should throw NullPointerException when sources is null")
  void shouldThrowWhenSourcesIsNull() {
    assertThrows(NullPointerException.class, () -> merger.merge(null));
  }

  @Test
  @DisplayName("should return unmodifiable empty map when sources is empty")
  void shouldReturnUnmodifiableEmptyMapWhenSourcesIsEmpty() {
    Map<String, Object> result = merger.merge(Collections.emptyList());
    assertTrue(result.isEmpty());
    assertThrows(UnsupportedOperationException.class, () -> result.put("another", "test"));
  }

  @Test
  @DisplayName("should return unmodifiable map with single entry when single non-empty map is provided")
  void shouldReturnUnmodifiableMapWhenSingleMapProvided() {
    Map<String, Object> input = Map.of("key", "value");
    Map<String, Object> result = merger.merge(List.of(input));

    assertEquals("value", result.get("key"));
    assertThrows(UnsupportedOperationException.class, () -> result.put("another", "test"));
  }

  @Test
  @DisplayName("should return unmodifiable empty map when single element is null")
  void shouldReturnUnmodifiableEmptyMapWhenSingleElementIsNull() {
    Map<String, Object> result = merger.merge(Collections.singletonList(null));
    assertTrue(result.isEmpty());
    assertThrows(UnsupportedOperationException.class, () -> result.put("another", "test"));
  }

  @Test
  @DisplayName("should merge multiple maps with overriding behavior and return an unmodifiable map")
  void shouldMergeMultipleMapsWithOverridingBehaviorAndReturnUnmodifiableMap() {
    Map<String, Object> first = Map.of("key1", "value1", "key2", List.of(1, 2));
    Map<String, Object> second = Map.of("key2", List.of(3, 4), "key3", "value3");

    Map<String, Object> result = merger.merge(List.of(first, second));

    assertEquals("value1", result.get("key1"));
    assertEquals(List.of(3, 4), result.get("key2"));
    assertEquals("value3", result.get("key3"));
    assertThrows(UnsupportedOperationException.class, () -> result.put("another", "test"));
  }

  @Test
  @DisplayName("should ignore null and empty maps in the list")
  void shouldIgnoreNullAndEmptyMaps() {
    Map<String, Object> empty = Collections.emptyMap();
    var sources = new ArrayList<Map<String, Object>>();
    sources.add(null);
    sources.add(empty);
    sources.add(Map.of("key1", "value1"));

    Map<String, Object> result = merger.merge(sources);

    assertEquals("value1", result.get("key1"));
    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("should keep non-array values unchanged")
  void shouldKeepNonArrayValuesUnchanged() {
    Map<String, Object> first = Map.of("env", "stage");
    Map<String, Object> result = merger.merge(List.of(first, Collections.emptyMap()));
    assertEquals("stage", result.get("env"));
  }

  @Test
  @DisplayName("should deeply merge nested maps")
  void shouldDeeplyMergeNestedMaps() {
    Map<String, Object> first = Map.of("config", Map.of("timeout", 10, "retries", 3));
    Map<String, Object> second = Map.of("config", Map.of("timeout", 20));

    Map<String, Object> result = merger.merge(List.of(first, second));

    Map<String, Object> config = (Map<String, Object>) result.get("config");

    assertEquals(20, config.get("timeout"));
    assertEquals(3, config.get("retries"));
  }

  @Test
  @DisplayName("should throw ConversionException when source map cannot be converted")
  void shouldThrowConversionExceptionWhenSourceMapCannotBeConverted() {
    var objectMapper = spy(new ObjectMapper());
    var merger = new JacksonMerger(objectMapper);

    doThrow(IllegalArgumentException.class).when(objectMapper)
        .convertValue(any(Map.class), eq(ObjectNode.class));

    Map<String, Object> source = new HashMap<>();
    source.put("key", "value");

    List<Map<String, Object>> sources = List.of(source, Collections.emptyMap());

    ConversionException ex = assertThrows(ConversionException.class, () -> merger.merge(sources));
    assertTrue(ex.getMessage().contains("failed to prepare data for merging"));
    assertNotNull(ex.getCause());
  }

  @Test
  @DisplayName("should throw MergeException when merging data structures fails")
  void shouldThrowMergeExceptionWhenMergingFails() throws IOException {
    var objectMapper = spy(new ObjectMapper());
    var merger = new JacksonMerger(objectMapper);
    var mergedNode = objectMapper.createObjectNode();
    var readerSpy = spy(objectMapper.readerForUpdating(mergedNode));

    doThrow(IOException.class).when(readerSpy).readValue(any(ObjectNode.class));
    doReturn(readerSpy).when(objectMapper).readerForUpdating(mergedNode);

    Map<String, Object> source = Map.of("key", "value");
    List<Map<String, Object>> sources = List.of(source, Collections.emptyMap());

    MergeException ex = assertThrows(MergeException.class, () -> merger.merge(sources));

    assertTrue(ex.getMessage().contains("error merging data structures"));
    assertNotNull(ex.getCause());
    assertEquals(IOException.class, ex.getCause().getClass());
  }

  @Test
  @DisplayName("should throw ConversionException when finalizing merged result fails")
  void shouldThrowConversionExceptionWhenFinalizingMergedResultFails() {
    var objectMapper = spy(new ObjectMapper());
    var merger = new JacksonMerger(objectMapper);

    doReturn(objectMapper.createObjectNode()).when(objectMapper)
        .convertValue(any(Map.class), eq(ObjectNode.class));

    doThrow(IllegalArgumentException.class)
        .when(objectMapper)
        .convertValue(any(ObjectNode.class), Mockito.<TypeReference<Map<String, Object>>>any());

    Map<String, Object> source = Map.of("key", "value");
    List<Map<String, Object>> sources = List.of(source, Collections.emptyMap());

    ConversionException ex = assertThrows(ConversionException.class, () -> merger.merge(sources));

    assertTrue(ex.getMessage().contains("failed to finalize merged result"));
    assertEquals(IllegalArgumentException.class, ex.getCause().getClass());
  }

  @Test
  @DisplayName("should be thread-safe when merging simple values from multiple threads")
  void shouldBeThreadSafeWhenMergingSimpleValuesFromMultipleThreads() {
    Map<String, Object> first = Map.of("env", "stage");
    Map<String, Object> second = Map.of("env", "prod");
    List<Map<String, Object>> sources = List.of(first, second);

    List<Map<String, Object>> results =
        IntStream.range(0, 50)
            .parallel()
            .mapToObj(i -> merger.merge(sources))
            .toList();

    results.forEach(result -> {
      assertEquals("prod", result.get("env"));
      assertThrows(UnsupportedOperationException.class, () -> result.put("env", "dev"));
    });
  }

  @Test
  @DisplayName("should be thread-safe when called from multiple threads")
  void shouldBeThreadSafeWhenCalledFromMultipleThreads() {
    Map<String, Object> first = Map.of("key1", List.of(3, 4));
    Map<String, Object> second = Map.of("key1", List.of(5, 6));
    List<Map<String, Object>> sources = List.of(first, second);

    List<Map<String, Object>> results =
        IntStream.range(0, 50)
            .parallel()
            .mapToObj(i -> merger.merge(sources))
            .toList();

    results.forEach(result -> {
      assertEquals(List.of(5, 6), result.get("key1"));
      assertThrows(UnsupportedOperationException.class, () -> result.put("key1", List.of(7, 8)));
    });
  }

  @Test
  @DisplayName("should throw on remove and clear to enforce immutability")
  void shouldThrowOnRemoveAndClear() {
    Map<String, Object> first = Map.of("key1", List.of(3, 4));
    Map<String, Object> result = merger.merge(List.of(first));
    assertThrows(UnsupportedOperationException.class, () -> result.remove("key1"));
    assertThrows(UnsupportedOperationException.class, result::clear);
  }
}
