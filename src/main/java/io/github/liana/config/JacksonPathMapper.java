package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.exception.ConversionException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class that provides safe, type-aware access to JSON data using dot-separated paths and
 * Jackson's conversion capabilities.
 *
 * <p>This class converts arbitrary Java objects into a Jackson {@link JsonNode}
 * tree, navigates the tree using JSON Pointer expressions generated from paths, and converts the
 * resulting nodes into strongly-typed Java objects.
 *
 * <p>All methods guarantee:
 * <ul>
 *   <li>Null-safety: all arguments are validated, throwing {@link NullPointerException}
 *       if any required parameter is {@code null}.</li>
 *   <li>Type-safety: conversion errors are wrapped and rethrown as
 *       {@link ConversionException} with informative messages.</li>
 *   <li>Immutability: returned lists and maps are safe to use and do not affect
 *       internal state.</li>
 *   <li>Graceful behavior: missing paths return {@link Optional#empty()},
 *       {@link java.util.Collections#emptyList()}, or
 *       {@link java.util.Collections#emptyMap()} depending on the method.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 *
 * <pre>{@code
 * ObjectMapper mapper = new ObjectMapper();
 * JacksonPathMapper pathMapper = new JacksonPathMapper(mapper);
 *
 * // Example source object
 * Map<String, Object> source = Map.of(
 *     "app", Map.of(
 *         "name", "my-service",
 *         "port", 8080,
 *         "features", List.of("logging", "metrics")
 *     )
 * );
 *
 * // 1. Get the entire object as a typed map
 * Optional<Map<String, Object>> all = pathMapper.get(source, new TypeReference<>() {}.getType());
 *
 * // 2. Get a single property
 * Optional<String> appName = pathMapper.get(source, "app.name", String.class);
 *
 * // 3. Get a list of enabled features
 * List<String> features = pathMapper.getList(source, "app.features", String.class);
 *
 * // 4. Get a nested map
 * Map<String, Object> appConfig = pathMapper.getMap(source, "app", Object.class);
 *
 * // 5. Check if a path exists
 * boolean hasPort = pathMapper.hasPath(source, "app.port");
 * }</pre>
 *
 * <p>This class is immutable and thread-safe.
 */
final class JacksonPathMapper extends AbstractJacksonComponent {

  private static final String MSG_SOURCE_NULL = "source object cannot be null";
  private static final String MSG_PATH_NULL = "path cannot be null";
  private static final String MSG_TARGET_TYPE_NULL = "target type cannot be null";
  private static final String MSG_INVALID_TARGET_LIST_TYPE = "invalid or unsupported target list type: %s";
  private static final String MSG_INVALID_TARGET_MAP_TYPE = "invalid or unsupported target map type: %s";
  private static final String MSG_INVALID_TARGET_TYPE = "invalid or unsupported target type: %s";
  private static final String MSG_INVALID_JSON_POINTER = "invalid JSON pointer generated from path: %s";
  private static final String MSG_CONVERT_SOURCE_TO_TREE =
      "failed to convert source to JSON tree. Source type: %s";
  private static final String MSG_CONVERT_VALUE =
      "failed to convert value to target type: %s";

  /**
   * Creates a new {@code JacksonPathMapper} using the given {@link ObjectMapper}.
   *
   * @param mapper the object mapper to use for converting values and constructing types
   * @throws NullPointerException if {@code mapper} is null
   */
  JacksonPathMapper(ObjectMapper mapper) {
    super(mapper);
  }

  /**
   * Retrieves the entire source object as an instance of the specified {@code targetType}.
   *
   * @param source     the source object to convert
   * @param targetType the type to convert the source into
   * @param <T>        the target type
   * @return an {@link Optional} containing the converted value, or empty if the node is missing
   * @throws NullPointerException if {@code source} or {@code targetType} is null
   * @throws ConversionException  if conversion to the target type fails
   */
  public <T> Optional<T> get(Object source, Type targetType) {
    requireNonNull(source, MSG_SOURCE_NULL);
    requireNonNull(targetType, MSG_TARGET_TYPE_NULL);

    JsonNode node = getNode(source);
    if (node.isMissingNode()) {
      return Optional.empty();
    }

    return Optional.ofNullable(convertValue(node, constructJavaType(targetType)));
  }

  /**
   * Retrieves the value located at the given path as an instance of the specified
   * {@code targetType}.
   *
   * @param source     the source object to convert
   * @param path       the dot-notated path to the value (supports array indices, e.g.
   *                   {@code users[0].name})
   * @param targetType the type to convert the value into
   * @param <T>        the target type
   * @return an {@link Optional} containing the converted value, or empty if the path does not exist
   * @throws NullPointerException if {@code source}, {@code path}, or {@code targetType} is null
   * @throws ConversionException  if conversion to the target type fails or the path is invalid
   */
  public <T> Optional<T> get(Object source, String path, Type targetType) {
    requireNonNull(source, MSG_SOURCE_NULL);
    requireNonNull(path, MSG_PATH_NULL);
    requireNonNull(targetType, MSG_TARGET_TYPE_NULL);

    JsonNode node = getNode(source, path);
    if (node.isMissingNode()) {
      return Optional.empty();
    }

    return Optional.ofNullable(convertValue(node, constructJavaType(targetType)));
  }

  /**
   * Retrieves a list of values located at the given path, converting each element to the specified
   * type.
   *
   * @param source     the source object to convert
   * @param path       the dot-notated path to the array
   * @param targetType the type of the elements in the list
   * @param <E>        the element type
   * @return a {@link List} of converted values, or an empty list if the path does not exist or is
   * not an array
   * @throws NullPointerException if {@code source}, {@code path}, or {@code targetType} is null
   * @throws ConversionException  if the target type is invalid or conversion fails
   */
  public <E> List<E> getList(Object source, String path, Class<E> targetType) {
    requireNonNull(source, MSG_SOURCE_NULL);
    requireNonNull(path, MSG_PATH_NULL);
    requireNonNull(targetType, MSG_TARGET_TYPE_NULL);

    JsonNode node = getNode(source, path);
    if (node.isMissingNode() || !node.isArray()) {
      return Collections.emptyList();
    }

    JavaType listType = executeWithResult(
        () -> mapper.getTypeFactory().constructCollectionType(List.class, targetType),
        String.format(MSG_INVALID_TARGET_LIST_TYPE, targetType.getName())
    );

    List<E> list = convertValue(node, listType);
    return Optional.ofNullable(list).orElse(Collections.emptyList());
  }

  /**
   * Retrieves a map of values located at the given path, converting each value to the specified
   * type.
   *
   * @param source     the source object to convert
   * @param path       the dot-notated path to the object
   * @param targetType the type of the values in the map
   * @param <V>        the value type
   * @return a {@link Map} of converted values keyed by property name, or an empty map if the path
   * does not exist or is not an object
   * @throws NullPointerException if {@code source} or {@code path} is null
   * @throws ConversionException  if the target type is invalid or conversion fails
   */
  public <V> Map<String, V> getMap(Object source, String path, Class<V> targetType) {
    requireNonNull(source, MSG_SOURCE_NULL);
    requireNonNull(path, MSG_PATH_NULL);

    JsonNode node = getNode(source, path);
    if (node.isMissingNode() || !node.isObject()) {
      return Collections.emptyMap();
    }

    JavaType mapType = executeWithResult(
        () -> mapper.getTypeFactory().constructMapType(Map.class, String.class, targetType),
        String.format(MSG_INVALID_TARGET_MAP_TYPE, targetType.getName())
    );

    Map<String, V> resultMap = convertValue(node, mapType);
    return Optional.ofNullable(resultMap).orElse(Collections.emptyMap());
  }

  /**
   * Checks if a value exists at the given path.
   *
   * @param source the source object to convert
   * @param path   the dot-notated path to check
   * @return {@code true} if the path exists and is not a missing node, {@code false} otherwise
   * @throws NullPointerException if {@code source} or {@code path} is null
   * @throws ConversionException  if the path cannot be converted to a valid JSON pointer or if
   *                              conversion to a {@link JsonNode} fails
   */
  public boolean hasPath(Object source, String path) {
    requireNonNull(source, MSG_SOURCE_NULL);
    requireNonNull(path, MSG_PATH_NULL);

    JsonNode node = getNode(source, path);
    return !node.isMissingNode();
  }

  /**
   * Retrieves a {@link JsonNode} at the given path from the provided source object.
   *
   * @param source the source object
   * @param path   the dot-notated path
   * @return the {@link JsonNode} at the specified path
   * @throws ConversionException if the path cannot be converted to a JSON pointer
   */
  private JsonNode getNode(Object source, String path) {
    JsonNode tree = getNode(source);
    String jsonPointer = toJsonPointer(path);
    return executeWithResult(() -> tree.at(jsonPointer),
        String.format(MSG_INVALID_JSON_POINTER, path));
  }

  /**
   * Converts the source object into a {@link JsonNode} tree.
   *
   * @param source the source object
   * @return the corresponding {@link JsonNode} tree
   * @throws ConversionException if conversion fails
   */
  private JsonNode getNode(Object source) {
    return executeWithResult(
        () -> mapper.valueToTree(source),
        String.format(MSG_CONVERT_SOURCE_TO_TREE, source.getClass().getName())
    );
  }

  /**
   * Constructs a Jackson {@link JavaType} from a {@link Type}.
   *
   * @param targetType the type to construct
   * @return the corresponding {@link JavaType}
   * @throws ConversionException if the type cannot be constructed
   */
  private JavaType constructJavaType(Type targetType) {
    return executeWithResult(
        () -> mapper.constructType(targetType),
        String.format(MSG_INVALID_TARGET_TYPE, targetType.getTypeName())
    );
  }

  /**
   * Converts a dot-notated path into a JSON Pointer.
   *
   * @param path the dot-notated path (e.g. {@code user.addresses[0].city})
   * @return a valid JSON pointer string (e.g. {@code /user/addresses/0/city})
   */
  private String toJsonPointer(String path) {
    return "/" + path.replace(".", "/")
        .replaceAll("\\[(\\d+)]", "/$1");
  }

  /**
   * Converts a value into an instance of the specified {@link JavaType}.
   *
   * @param value the value to convert
   * @param type  the target type
   * @param <T>   the result type
   * @return the converted value
   * @throws ConversionException if the value cannot be converted
   */
  private <T> T convertValue(Object value, JavaType type) {
    return executeWithResult(
        () -> mapper.convertValue(value, type),
        String.format(MSG_CONVERT_VALUE, type.getTypeName()));
  }
}
