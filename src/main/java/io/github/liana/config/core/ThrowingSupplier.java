package io.github.liana.config.core;

public interface ThrowingSupplier<T> {
  T get() throws Exception;
}
