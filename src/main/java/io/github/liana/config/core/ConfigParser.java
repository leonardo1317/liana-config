package io.github.liana.config.core;

import io.github.liana.config.api.Configuration;
import java.io.IOException;
import java.io.InputStream;

public interface ConfigParser {

  Configuration parse(InputStream inputStream) throws IOException;
}
