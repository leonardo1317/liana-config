package io.github.liana.config;

import java.io.IOException;
import java.io.InputStream;

public interface ConfigParser {

  Configuration parse(InputStream inputStream) throws IOException;
}
