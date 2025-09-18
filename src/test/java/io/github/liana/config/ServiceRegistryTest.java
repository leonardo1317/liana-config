package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.BiPredicate;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceRegistryTest {

  @Mock
  private ServiceLoader<String> loader;
  private BiPredicate<String, String> filter;
  private Function<String, String> function;

  @BeforeEach
  void setUp() {
    filter = String::equalsIgnoreCase;
    function = service -> service;
  }

  @Test
  @DisplayName("should return mapped instance when service matches type")
  void shouldReturnMappedInstanceWhenServiceMatchesType() {
    Iterable<String> iterable = () -> List.of("json").iterator();
    when(loader.iterator()).thenReturn(iterable.iterator());
    ServiceRegistry<String, String> registry = new ServiceRegistry<>(loader, filter, function);

    Optional<String> result = registry.get("json");

    assertTrue(result.isPresent());
    assertEquals("json", result.get());
  }

  @Test
  @DisplayName("should return empty when no service matches type")
  void shouldReturnEmptyWhenNoServiceMatchesType() {
    Iterable<String> iterable = () -> List.of("json").iterator();
    when(loader.iterator()).thenReturn(iterable.iterator());
    ServiceRegistry<String, String> registry = new ServiceRegistry<>(loader, filter, function);

    Optional<String> result = registry.get("yaml");

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should return cached instance on repeated calls")
  void shouldReturnCachedInstanceOnRepeatedCalls() {
    Iterable<String> iterable = () -> List.of("json").iterator();
    when(loader.iterator()).thenReturn(iterable.iterator());
    ServiceRegistry<String, String> registry = new ServiceRegistry<>(loader, filter, function);

    String first = registry.get("json").orElseThrow();
    String second = registry.get("json").orElseThrow();

    assertSame(first, second);
  }

  @Test
  @DisplayName("should return different cached instances for different types")
  void shouldReturnDifferentCachedInstancesForDifferentTypes() {
    Iterable<String> iterable = () -> List.of("json", "yaml").iterator();
    when(loader.iterator()).thenReturn(iterable.iterator());
    ServiceRegistry<String, String> registry = new ServiceRegistry<>(loader, filter, function);

    String jsonMapper = registry.get("json").orElseThrow();
    String yamlMapper = registry.get("yaml").orElseThrow();

    assertNotNull(jsonMapper);
    assertNotNull(yamlMapper);
    assertNotSame(jsonMapper, yamlMapper);

    assertSame(jsonMapper, registry.get("json").orElseThrow());
    assertSame(yamlMapper, registry.get("yaml").orElseThrow());
  }

  @Test
  @DisplayName("should throw when loader is null")
  void shouldThrowWhenLoaderIsNull() {
    assertThrows(NullPointerException.class, () -> new ServiceRegistry<>(null, filter, function));
  }

  @Test
  @DisplayName("should throw when filter is null")
  void shouldThrowWhenFilterIsNull() {
    assertThrows(NullPointerException.class, () -> new ServiceRegistry<>(loader, null, function));
  }

  @Test
  @DisplayName("should throw when function is null")
  void shouldThrowWhenFunctionIsNull() {
    assertThrows(NullPointerException.class, () -> new ServiceRegistry<>(loader, filter, null));
  }

  @Test
  @DisplayName("should throw when type is null in get")
  void shouldThrowWhenTypeIsNullInGet() {
    ServiceRegistry<String, String> registry = new ServiceRegistry<>(loader, filter, function);

    assertThrows(NullPointerException.class, () -> registry.get(null));
  }
}
