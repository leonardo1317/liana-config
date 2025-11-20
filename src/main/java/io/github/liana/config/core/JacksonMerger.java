package io.github.liana.config.core;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.liana.config.core.exception.ConversionException;
import io.github.liana.config.core.exception.MergeException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class that merges multiple {@link Map} instances into a single unified structure.
 *
 * <p>This class converts each input map into a Jackson {@link ObjectNode}, performs a deep merge,
 * and overrides arrays entirely (rather than merging elements individually). When a key appears in
 * multiple maps, the value from the last map wins.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * ObjectMapper mapper = new ObjectMapper();
 * JacksonMerger merger = new JacksonMerger(mapper);
 *
 * Map<String, Object> first = Map.of("key1", "value1", "key2", List.of(1, 2));
 * Map<String, Object> second = Map.of("key2", List.of(3, 4), "key3", "value3");
 *
 * Map<String, Object> result = merger.merge(List.of(first, second));
 *
 * // result:
 * // {
 * //   "key1": "value1",
 * //   "key2": [3, 4],  // overridden by second map
 * //   "key3": "value3"
 * // }
 * }</pre>
 *
 * <p>This class is immutable and thread-safe, provided the underlying {@link ObjectMapper} is
 * thread-safe.
 */
public final class JacksonMerger extends AbstractJacksonComponent {

  /**
   * Creates a new {@code JacksonMerger} with the given {@link ObjectMapper}.
   *
   * @param mapper the object mapper used for JSON conversions; must not be null
   * @throws NullPointerException if {@code mapper} is null
   */
  JacksonMerger(ObjectMapper mapper) {
    super(mapper);
  }

  /**
   * Merges a list of configuration maps into a single immutable result.
   *
   * <p>Null and empty maps are ignored. If the list is empty, an empty map is returned.
   * If the list contains only one map, an unmodifiable view of that map is returned.</p>
   *
   * @param sources list of maps to merge; must not be null
   * @return a consolidated and immutable map containing the merged data
   * @throws NullPointerException if {@code sources} is null
   * @throws ConversionException  if a conversion error occurs during merging
   * @throws MergeException       if a runtime error occurs while applying updates
   */
  public Map<String, Object> merge(List<Map<String, Object>> sources) {
    requireNonNull(sources, "sources list must not be null");

    if (sources.isEmpty()) {
      return Collections.emptyMap();
    }

    if (sources.size() == 1) {
      return Optional.ofNullable(sources.get(0))
          .map(Collections::unmodifiableMap)
          .orElse(Collections.emptyMap());
    }

    ObjectNode mergedNode = mapper.createObjectNode();

    sources.stream()
        .filter(source -> nonNull(source) && !source.isEmpty())
        .forEach(source -> mergeSourceIntoNode(source, mergedNode));

    return Collections.unmodifiableMap(
        executeWithResult(() -> mapper.convertValue(mergedNode, MAP_TYPE),
            "failed to finalize merged result"));
  }

  /**
   * Merges a single source map into the target node.
   *
   * <p>This method converts the given map into an {@link ObjectNode}, overrides any
   * arrays in the target node, and applies the updates. Any conversion or I/O error is wrapped in a
   * consistent runtime exception.</p>
   *
   * @param source     the map to merge; must not be null
   * @param mergedNode the target node to update; must not be null
   * @throws ConversionException if the map cannot be converted to an {@link ObjectNode}
   * @throws MergeException      if an error occurs while applying updates
   */
  private void mergeSourceIntoNode(Map<String, Object> source, ObjectNode mergedNode) {
    ObjectNode current = executeWithResult(
        () -> mapper.convertValue(source, ObjectNode.class),
        "failed to prepare data for merging");

    overrideArrays(mergedNode, current);

    executeAction(() -> mapper.readerForUpdating(mergedNode).readValue(current),
        "error merging data structures");
  }

  /**
   * Replaces any array fields in the target node with the corresponding arrays from the current
   * node.
   *
   * <p>This method iterates over all fields in {@code current}. When an array
   * field is found, its value is copied into {@code mergedNode} and removed from {@code current} so
   * it will not be processed by the subsequent merge step. This ensures arrays are completely
   * overridden rather than merged element by element.</p>
   *
   * @param mergedNode the target node to update; must not be null
   * @param current    the node containing new values; must not be null
   */
  private void overrideArrays(ObjectNode mergedNode, ObjectNode current) {
    Iterator<String> fieldNames = current.fieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      JsonNode value = current.get(fieldName);
      if (value.isArray()) {
        mergedNode.set(fieldName, value);
        fieldNames.remove();
      }
    }
  }
}
