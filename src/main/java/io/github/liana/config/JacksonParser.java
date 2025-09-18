package io.github.liana.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

final class JacksonParser extends AbstractJacksonComponent implements ConfigParser {

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
