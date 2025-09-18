package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

abstract class AbstractConfiguration implements Configuration {

  private final Map<String, Object> nestedMap;
  private final JacksonPathMapper mapper;

  protected AbstractConfiguration(Map<String, Object> nestedMap) {
    requireNonNull(nestedMap, "store type must not be null");
    this.nestedMap = Collections.unmodifiableMap(new LinkedHashMap<>(nestedMap));
    this.mapper = new JacksonPathMapper(JacksonMappers.create().getJson());
  }

  @Override
  public boolean hasKey(String key) {
    return mapper.hasPath(nestedMap, key);
  }

  @Override
  public <T> Optional<T> get(String key, Type type) {
    return mapper.get(nestedMap, key, type);
  }

  @Override
  public <E> List<E> getList(String key, Class<E> clazz) {
    List<E> result = mapper.getList(nestedMap, key, clazz);
    return result.isEmpty() ? Collections.emptyList() : List.copyOf(result);
  }

  @Override
  public <V> Map<String, V> getMap(String key, Class<V> clazz) {
    Map<String, V> result = mapper.getMap(nestedMap, key, clazz);
    return result.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(result);
  }

  @Override
  public Map<String, Object> getAllConfig() {
    return nestedMap;
  }

  @Override
  public <T> Optional<T> getAllConfigAs(Class<T> clazz) {
    return mapper.get(nestedMap, clazz);
  }
}
