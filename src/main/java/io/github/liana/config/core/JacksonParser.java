package io.github.liana.config.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.api.Configuration;
import java.io.IOException;
import java.io.InputStream;

public final class JacksonParser extends AbstractJacksonComponent implements ConfigParser {

  /**
   * Creates a new {@code JacksonParser} with the given {@link ObjectMapper}.
   *
   * @param mapper the object mapper used for JSON conversions; must not be null
   * @throws NullPointerException if {@code mapper} is null
   */
  JacksonParser(ObjectMapper mapper) {
    super(mapper);
  }

  @Override
  public Configuration parse(InputStream inputStream) throws IOException {
    return new JacksonConfiguration(mapper, inputStream);
  }
}
