package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JacksonConfigurationTest {

  @Mock
  private ObjectMapper mapper;

  @Test
  @DisplayName("should create JacksonConfiguration when mapper successfully reads input")
  void shouldCreateConfigurationWhenMapperReadsInput() throws Exception {
    InputStream input = new ByteArrayInputStream("{}".getBytes());

    Map<String, Object> dummyMap = Map.of("key", "value");
    when(mapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(dummyMap);

    JacksonConfiguration config = new JacksonConfiguration(mapper, input);

    assertNotNull(config);
    assertEquals(Optional.of("value"), config.get("key", String.class));
    verify(mapper).readValue(any(InputStream.class), any(TypeReference.class));
  }

  @Test
  @DisplayName("should throw IOException when mapper fails to read input")
  void shouldThrowIOExceptionWhenMapperFails() throws Exception {
    InputStream input = new ByteArrayInputStream("invalid".getBytes());

    when(mapper.readValue(any(InputStream.class), any(TypeReference.class)))
        .thenThrow(new IOException("fail"));

    assertThrows(IOException.class, () -> new JacksonConfiguration(mapper, input));
  }

  @Test
  @DisplayName("should throw NullPointerException when mapper is null")
  void shouldThrowNullPointerExceptionWhenMapperIsNull() {
    InputStream input = new ByteArrayInputStream("{}".getBytes());
    assertThrows(NullPointerException.class, () -> new JacksonConfiguration(null, input));
  }

  @Test
  @DisplayName("should throw NullPointerException when input is null")
  void shouldThrowNullPointerExceptionWhenInputIsNull() {
    ObjectMapper mapper = new ObjectMapper();
    assertThrows(NullPointerException.class, () -> new JacksonConfiguration(mapper, null));
  }
}
