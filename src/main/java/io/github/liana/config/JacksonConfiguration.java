package io.github.liana.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class JacksonConfiguration extends AbstractConfiguration {

  JacksonConfiguration(ObjectMapper mapper, InputStream input) throws IOException {
    super(read(mapper, input));
  }

  private static Map<String, Object> read(ObjectMapper mapper, InputStream input)
      throws IOException {
    requireNonNull(mapper, "ObjectMapper must not be null");
    requireNonNull(input, "InputStream must not be null");
    return mapper.readValue(input, new TypeReference<>() {
    });
  }
}
