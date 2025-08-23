package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

import io.github.liana.internal.ImmutableConfigMap;
import io.github.liana.internal.ImmutableConfigSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ConfigResourcePreparerTest {

  @Mock
  private ConfigResourceLocation configResourceLocation;
  private ConfigResourcePreparer preparer;

  @BeforeEach
  void setUp() {
    preparer = new ConfigResourcePreparer(configResourceLocation);
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("should throw when ConfigResourceLocation is null")
    void shouldThrowWhenConfigResourceLocationIsNull() {
      assertThrows(NullPointerException.class, () -> new ConfigResourcePreparer(null));
    }

    @Test
    @DisplayName("should use default profile when profile is null")
    void shouldUseDefaultProfileWhenProfileIsNull() {
      final String PROFILE = "default";
      ConfigResourcePreparer preparer = new ConfigResourcePreparer(configResourceLocation, null);

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream().anyMatch(resource -> resource.getResourceName().contains(PROFILE))
      );
    }

    @Test
    @DisplayName("should use default profile when profile is empty")
    void shouldUseDefaultProfileWhenProfileIsEmpty() {
      final String PROFILE = "default";
      ConfigResourcePreparer preparer = new ConfigResourcePreparer(configResourceLocation, "");

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream().anyMatch(resource -> resource.getResourceName().contains(PROFILE))
      );
    }

    @Test
    @DisplayName("should assign profile correctly when profile is provided")
    void shouldAssignProfileCorrectlyWhenProfileIsProvided() {
      final String PROFILE = "dev";
      ConfigResourcePreparer preparer = new ConfigResourcePreparer(configResourceLocation, PROFILE);

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream().anyMatch(resource -> resource.getResourceName().contains(PROFILE))
      );
    }

  }

  @Nested
  @DisplayName("Provider Tests")
  class ProviderTests {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("should use default 'classpath' when provider is null or empty")
    void shouldUseDefaultProviderWhenProviderIsNullOrEmpty(String invalidProvider) {
      final String DEFAULT_PROVIDER = "classpath";
      when(configResourceLocation.getProvider()).thenReturn(invalidProvider);

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream().anyMatch(resource -> DEFAULT_PROVIDER.equals(resource.getProvider()))
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("should use default 'classpath' when provider contains only whitespace")
    void shouldUseDefaultProviderWhenProviderIsBlank(String provider) {
      final String DEFAULT_PROVIDER = "classpath";
      when(configResourceLocation.getProvider()).thenReturn(provider);

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream().anyMatch(resource -> DEFAULT_PROVIDER.equals(resource.getProvider()))
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"git", "classpath", "github"})
    @DisplayName("should use provided provider when not blank")
    void shouldUseProvidedProviderWhenNotBlank(String provider) {
      final String RESOURCE_NAME = "application.properties";
      when(configResourceLocation.getProvider()).thenReturn(provider);
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(Set.of(RESOURCE_NAME)));

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream().anyMatch(resource -> provider.equals(resource.getProvider()))
      );
    }
  }

  @Nested
  @DisplayName("ResourceNames Tests")
  class ResourceNamesTests {

    @ParameterizedTest
    @NullSource
    @DisplayName("should use default resource names when provided name is null")
    void shouldUseDefaultResourceNamesWhenProvidedNameIsNull(String resourceName) {
      final String EXTENSION = "properties";
      final String PROFILE = "default";
      final String RESOURCE_NAME = "application" + "." + EXTENSION;
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
      Set<String> resourceNames = new HashSet<>();
      resourceNames.add(resourceName);
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      try (
          MockedStatic<ClasspathResource> mockedClasspath = mockStatic(ClasspathResource.class);
          MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(
              FilenameValidator.class)
      ) {
        mockedClasspath.when(() -> ClasspathResource.resourceExists(anyString()))
            .thenReturn(true);

        mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(anyString()))
            .thenReturn(true);

        List<ConfigResourceReference> result = preparer.prepare();

        assertTrue(
            result.stream().anyMatch(resource -> RESOURCE_NAME.equals(resource.getResourceName()))
        );

        assertTrue(
            result.stream()
                .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
        );
      }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("should use default resource names when provided name is blank")
    void shouldUseDefaultResourceNamesWhenProvidedNameIsBlank(String resourceName) {
      final String EXTENSION = "properties";
      final String PROFILE = "default";
      final String RESOURCE_NAME = "application" + "." + EXTENSION;
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;

      Set<String> resourceNames = new HashSet<>();
      resourceNames.add(resourceName);
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      try (
          MockedStatic<ClasspathResource> mockedClasspath = mockStatic(ClasspathResource.class);
          MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(
              FilenameValidator.class)
      ) {

        mockedClasspath.when(() -> ClasspathResource.resourceExists(anyString()))
            .thenReturn(true);

        mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(anyString()))
            .thenReturn(true);

        List<ConfigResourceReference> result = preparer.prepare();

        assertTrue(
            result.stream().anyMatch(resource -> RESOURCE_NAME.equals(resource.getResourceName()))
        );

        assertTrue(
            result.stream()
                .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
        );
      }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("should return an empty list when default resource name resolution fails")
    void shouldReturnAnEmptyListWhenDefaultResourceNameResolutionFails(String resourceName) {
      Set<String> resourceNames = new HashSet<>();
      resourceNames.add(resourceName);
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      try (
          MockedStatic<ClasspathResource> mockedClasspath = mockStatic(ClasspathResource.class);
          MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(
              FilenameValidator.class)
      ) {
        mockedClasspath.when(() -> ClasspathResource.resourceExists(anyString()))
            .thenReturn(false);

        mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(anyString()))
            .thenReturn(false);

        List<ConfigResourceReference> result = preparer.prepare();

        assertTrue(result.isEmpty());
      }
    }

    @Test
    @DisplayName("should resolve resource name when valid names is provided")
    void shouldResolveResourceNameWhenValidNamesIsProvided() {
      final String EXTENSION = "properties";
      final String PROFILE = "default";
      final String RESOURCE_NAME = "application" + "." + EXTENSION;
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;

      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add("application.properties");
      resourceNames.add("application-${profile}.properties");

      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      try (
          MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(
              FilenameValidator.class)
      ) {

        mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(anyString()))
            .thenReturn(true);

        List<ConfigResourceReference> result = preparer.prepare();

        assertTrue(
            result.stream().anyMatch(resource -> RESOURCE_NAME.equals(resource.getResourceName()))
        );

        assertTrue(
            result.stream()
                .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
        );
      }
    }

    @DisplayName("should only include valid resource names")
    @Test
    void shouldOnlyIncludeValidResourceNames() {
      final String VALID_RESOURCE_NAME = "application.properties";
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add(VALID_RESOURCE_NAME);
      resourceNames.add("../invalid.json");

      when(configResourceLocation.getProvider()).thenReturn("classpath");
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      try (
          MockedStatic<FilenameValidator> validator = mockStatic(FilenameValidator.class);
      ) {
        validator.when(() -> FilenameValidator.isSafeResourceName(VALID_RESOURCE_NAME))
            .thenReturn(true);
        validator.when(() -> FilenameValidator.isSafeResourceName("../invalid.json"))
            .thenReturn(false);

        List<ConfigResourceReference> result = preparer.prepare();

        assertEquals(1, result.size());
        assertTrue(
            result.stream()
                .anyMatch(resource -> VALID_RESOURCE_NAME.equals(resource.getResourceName()))
        );
      }
    }

    @Test
    @DisplayName("should return an empty list when resource names are invalid and default provider is used")
    void shouldReturnAnEmptyListWhenResourceNamesAreInvalidAndDefaultProviderIsUsed() {
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add("/application/${profile}.properties");
      resourceNames.add("../config../.json");

      when(configResourceLocation.getProvider()).thenReturn("classpath");
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      try (
          MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(
              FilenameValidator.class)
      ) {
        mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(anyString()))
            .thenReturn(false);

        List<ConfigResourceReference> result = preparer.prepare();

        assertTrue(result.isEmpty());
      }
    }

    @Test
    @DisplayName("should return an empty list when resource names are invalid and non-default provider is used")
    void shouldReturnAnEmptyListWhenResourceNamesAreInvalidAndNonDefaultProviderIsUsed() {
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add("/application/${profile}.properties");
      resourceNames.add("../config../.json");

      when(configResourceLocation.getProvider()).thenReturn("git");
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      try (
          MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(
              FilenameValidator.class)
      ) {
        mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(anyString()))
            .thenReturn(false);

        List<ConfigResourceReference> result = preparer.prepare();

        assertTrue(result.isEmpty());
      }

    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("should return an empty list when resource names are null or empty with a non-default provider")
    void shouldReturnAnEmptyListWhenResourceNamesAreNullOrEmptyWithANonDefaultProvider(
        String resourceName) {
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add(resourceName);

      when(configResourceLocation.getProvider()).thenReturn("git");
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      try (
          MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(
              FilenameValidator.class)
      ) {
        mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(anyString()))
            .thenReturn(false);

        List<ConfigResourceReference> result = preparer.prepare();

        assertTrue(result.isEmpty());
      }
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("should return an empty list when resource names are blank with a non-default provider")
    void shouldReturnAnEmptyListWhenResourceNamesAreBlankWithANonDefaultProvider(
        String resourceName) {
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add(resourceName);

      when(configResourceLocation.getProvider()).thenReturn("git");
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      try (
          MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(
              FilenameValidator.class)
      ) {
        mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(anyString()))
            .thenReturn(false);

        List<ConfigResourceReference> result = preparer.prepare();

        assertTrue(result.isEmpty());
      }
    }
  }

  @Nested
  @DisplayName("Variables Tests")
  class VariablesTests {

    @Test
    @DisplayName("should use default profile variable when provider is default and variables are null")
    void shouldAddDefaultProfileVariableWhenDefaultProviderAndVariablesNull() {
      final String EXTENSION = "properties";
      final String PROFILE = "default";
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add("application-${profile}.properties");

      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(configResourceLocation.getVariables()).thenReturn(null);
      when(configResourceLocation.getPlaceholder()).thenReturn(Placeholders.builder().build());

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream()
              .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
      );
    }

    @Test
    @DisplayName("should use default profile variable when provider is default and variables are empty")
    void shouldAddDefaultProfileVariableWhenDefaultProviderAndVariablesEmpty() {
      final String EXTENSION = "properties";
      final String PROFILE = "default";
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add("application-${profile}.properties");

      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(configResourceLocation.getVariables()).thenReturn(ImmutableConfigMap.empty());
      when(configResourceLocation.getPlaceholder()).thenReturn(Placeholders.builder().build());

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream()
              .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
      );
    }

    @Test
    @DisplayName("should use provided variables when provider is not default")
    void shouldReturnProvidedVariablesWhenIsNotDefaultProvider() {
      final String EXTENSION = "properties";
      final String PROFILE = "dev";
      final String RESOURCE_NAME = "application-" + PROFILE + "." + EXTENSION;
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add("application-${profile}.properties");

      when(configResourceLocation.getProvider()).thenReturn("git");
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(configResourceLocation.getVariables()).thenReturn(
          ImmutableConfigMap.of(Map.of("profile", PROFILE)));
      when(configResourceLocation.getPlaceholder()).thenReturn(Placeholders.builder().build());

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream().anyMatch(resource -> RESOURCE_NAME.equals(resource.getResourceName()))
      );
    }

    @Test
    @DisplayName("should return empty resource name when profile variable is missing")
    void shouldSkipResourceWhenVariablesMissingForInterpolation() {
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add("application-${profile}.properties");
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(configResourceLocation.getVariables()).thenReturn(
          ImmutableConfigMap.of(Map.of("env", "dev")));
      when(configResourceLocation.getPlaceholder()).thenReturn(Placeholders.builder().build());

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should resolve resource name using profile variable when pattern is valid")
    void shouldResolveResourceNameAndExtensionWhenValid() {
      final String EXTENSION = "properties";
      final String PROFILE = "test";
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
      Set<String> resourceNames = new LinkedHashSet<>();
      resourceNames.add("application-${profile}.properties");
      when(configResourceLocation.getResourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(configResourceLocation.getVariables()).thenReturn(
          ImmutableConfigMap.of(Map.of("profile", PROFILE)));
      when(configResourceLocation.getPlaceholder()).thenReturn(Placeholders.builder().build());

      List<ConfigResourceReference> result = preparer.prepare();

      assertTrue(
          result.stream()
              .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
      );
    }
  }
}
