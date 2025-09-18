package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.liana.config.exception.ConfigProviderException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigResourceProviderTest {

  @Mock
  private StrategyRegistry<String, ConfigProvider> strategies;

  @Mock
  private ConfigProvider configProvider;

  @Mock
  private ConfigResourceReference resourceReference;

  @Mock
  private ConfigResource configResource;

  private ConfigResourceProvider provider;

  @BeforeEach
  void setUp() {
    provider = ConfigResourceProvider.of(strategies);
  }

  @Test
  @DisplayName("should create ConfigResourceProvider with non-null strategies")
  void shouldCreateWithNonNullStrategies() {
    assertNotNull(provider);
  }

  @Test
  @DisplayName("should throw NullPointerException when strategies is null")
  void shouldThrowWhenStrategiesIsNull() {
    assertThrows(NullPointerException.class, () -> ConfigResourceProvider.of(null));
  }

  @Test
  @DisplayName("should throw NullPointerException when resourceReference is null")
  void shouldThrowWhenResourceReferenceIsNull() {
    assertThrows(NullPointerException.class, () -> provider.resolve(null));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when provider is null")
  void shouldThrowWhenProviderIsNull() {
    when(resourceReference.provider()).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> provider.resolve(resourceReference));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when provider is blank")
  void shouldThrowWhenProviderIsBlank() {
    when(resourceReference.provider()).thenReturn("   ");
    assertThrows(IllegalArgumentException.class, () -> provider.resolve(resourceReference));
  }

  @Test
  @DisplayName("should throw ConfigProviderException when no provider is found in registry")
  void shouldThrowWhenProviderNotFound() {
    when(resourceReference.provider()).thenReturn("missing");
    when(strategies.get("missing")).thenReturn(Optional.empty());

    ConfigProviderException ex =
        assertThrows(ConfigProviderException.class, () -> provider.resolve(resourceReference));

    assertTrue(ex.getMessage().contains("no config provider found for provider missing"));
  }

  @Test
  @DisplayName("should delegate to ConfigProvider when provider exists")
  void shouldDelegateToConfigProvider() {
    when(resourceReference.provider()).thenReturn("valid");
    when(strategies.get("valid")).thenReturn(Optional.of(configProvider));
    when(configProvider.resolveResource(resourceReference)).thenReturn(configResource);

    ConfigResource result = provider.resolve(resourceReference);

    assertSame(configResource, result);
    verify(configProvider).resolveResource(resourceReference);
  }

  @Test
  @DisplayName("should rethrow ConfigProviderException from provider")
  void shouldRethrowConfigProviderException() {
    when(resourceReference.provider()).thenReturn("valid");
    when(strategies.get("valid")).thenReturn(Optional.of(configProvider));
    when(configProvider.resolveResource(resourceReference))
        .thenThrow(new ConfigProviderException("provider failed"));

    ConfigProviderException ex =
        assertThrows(ConfigProviderException.class, () -> provider.resolve(resourceReference));

    assertEquals("provider failed", ex.getMessage());
  }

  @Test
  @DisplayName("should wrap unexpected exception into ConfigProviderException")
  void shouldWrapUnexpectedException() {
    when(resourceReference.provider()).thenReturn("valid");
    when(strategies.get("valid")).thenReturn(Optional.of(configProvider));
    when(configProvider.resolveResource(resourceReference))
        .thenThrow(new RuntimeException("boom"));

    ConfigProviderException ex =
        assertThrows(ConfigProviderException.class, () -> provider.resolve(resourceReference));

    assertTrue(ex.getMessage().contains("unexpected error while obtaining provider for resource"));
    assertInstanceOf(RuntimeException.class, ex.getCause());
  }
}
