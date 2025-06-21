<img src="liana-logo-transparent.png" alt="Liana Logo" width="300"/>

# Liana Configuration Library

&#x20;

---

## Overview

**Liana** is a lightweight, framework-agnostic Java configuration library designed for simplicity and flexibility. Inspired by the liana plant that adapts to any structure, Liana adapts to your application's needs—**not the other way around**.

Liana abstracts configuration complexity and offers a unified API to load configurations from **YAML, JSON, XML, and Properties formats only**—without forcing the use of heavyweight frameworks.

---

## Philosophy

> "Tell me where your configuration files are, and I'll handle the rest."

Liana prioritizes:

- ⚡ **Minimalism**: No forced conventions or complex setups.
- 🔄 **Adaptability**: Works with supported formats and file structures.
- 🪶 **Simplicity**: One-time load with cache for fast repeated access.
- 🔍 **Clarity**: Supports placeholders, overrides, and variables.

---

## Supported Formats

- **YAML**
- **Properties**
- **JSON**
- **XML**

---

## Important Characteristics

- You can load **multiple files of different formats simultaneously** (e.g., YAML + JSON + Properties in the same configuration process).
- Configuration values follow **override precedence based on the order of resources defined in ConfigResourceLocation**. Later files can override values loaded earlier.
- **Placeholder substitution is fully flexible**: Not limited to profiles—users can define and substitute any custom placeholders (e.g., `${customVar}`) via the `addVariables` method.
- Supports **conversion of the entire configuration into POJO types** via `getAllConfigAs(Class<T> type)`.
- Supports **retrieval of complex generic types** via `TypeOf<T>` for lists, maps, or nested structures.
- Supports **conversion to POJO objects or generic structures using both the **``** / **``** methods**.
- Supports **optional and required retrieval variants** for POJOs and generics.
- Conversion to POJO objects or generic structures via `TypeOf<T>` or `getAllConfigAs(Class<T>)` is a key feature that simplifies direct mapping from configuration to Java objects.

---

## Installation

**Maven:**

```xml
<dependency>
  <groupId>com.example.liana</groupId>
  <artifactId>liana-core</artifactId>
  <version>1.0.0</version>
</dependency>
```

**Gradle:**

```groovy
dependencies {
  implementation 'com.example.liana:liana-core:1.0.0'
}
```

---

## Quick Start Example

### Define your configuration source:

```java
ConfigResourceLocation location = ConfigResourceLocation.builder()
    .addResources("application.properties", "application-${profile}.yaml")
    .addVariables("profile", "dev")
    .verboseLogging(true)
    .build();
```
### Example configuration file:

```yaml
app:
  name: Liana
servers:
  - host: "localhost"
    port: 8080
  - host: "example.com"
    port: 9090
```

### Example POJO classes:

```java
public class AppConfig {
    public String name;
}

public class ServerConfig {
    public String host;
    public int port;
}
```

### Load and read configuration:

```java
ConfigReader reader = ConfigFactory.load(location);
String appName = reader.getString("app.name", "DefaultApp");
int port = reader.getInt("server.port", 8080);
boolean debug = reader.getBoolean("debug.enabled", false);
```

### Load as POJO (Example):

```java
AppConfig config = reader.get("app", AppConfig.class, new AppConfig());

List<ServerConfig> servers = reader.get("servers", new TypeOf<List<ServerConfig>>() {}, List.of());
```

### Optional Variants:

```java
Optional<AppConfig> optionalConfig = reader.getOptional("app", new TypeOf<AppConfig>() {});

Optional<List<ServerConfig>> optionalServers = reader.getOptional("servers", new TypeOf<List<ServerConfig>>() {});
```

---

## ConfigResourceLocation API

### Builder Methods

| Method                          | Description                                                                                   | Example                                              |
| ------------------------------- | --------------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| `provider(String)`              | Sets the resource provider (e.g., "classpath"). **Defaults to "classpath" if not specified.** | `.provider("classpath")` *(optional)*                |
| `addResource(String)`           | Adds a single resource file to be loaded.                                                     | `.addResource("app.yaml")`                           |
| `addResources(String...)`       | Adds multiple resource files.                                                                 | `.addResources("app.yaml", "db.yaml")`               |
| `addVariables(String...)`       | Adds substitution variables using key-value pairs.                                            | `.addVariables("profile", "dev")`                    |
| `addVariablesFromMap(Map)`      | Adds substitution variables from a `Map<String, String>`.                                     | `.addVariablesFromMap(Map.of("profile", "prod"))`    |
| `addCredential(String, String)` | Adds a single credential key-value pair.                                                      | `.addCredential("accessKey", "****")`                |
| `addCredentials(String...)`     | Adds multiple credentials using key-value pairs as varargs.                                   | `.addCredentials("user", "admin", "pass", "secret")` |
| `addCredentialsFromMap(Map)`    | Adds credentials from a `Map<String, String>`.                                                | `.addCredentialsFromMap(Map.of("token", "abc"))`     |
| `verboseLogging(boolean)`       | Enables or disables verbose logging for detailed load process output.                         | `.verboseLogging(true)`                              |

---

## ConfigReader API

| Method                                               | Description                                                           | Example                                                                      |
| ---------------------------------------------------- | --------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| `get(String key, Class<T> type)`                     | Retrieves value for key, converted to specified type.                 | `reader.get("app.name", String.class)`                                       |
| `get(String key, TypeOf<T> typeOf)`                  | Retrieves value for key, converted to complex/generic type.           | `reader.get("servers", new TypeOf<List<String>>() {})`                       |
| `get(String key, Class<T> type, T defaultValue)`     | Retrieves value or returns default if missing.                        | `reader.get("timeout", Integer.class, 30)`                                   |
| `get(String key, TypeOf<T> typeOf, T defaultValue)`  | Retrieves value for key (generic type) or returns default if missing. | `reader.get("servers", new TypeOf<List<String>>() {}, List.of("localhost"))` |
| `getOptional(String key, Class<T> type)`             | Retrieves optional value for key and type.                            | `reader.getOptional("app.name", String.class)`                               |
| `getOptional(String key, TypeOf<T> typeOf)`          | Retrieves optional value for key and generic type.                    | `reader.getOptional("servers", new TypeOf<List<String>>() {})`               |
| `getString(String key)`                              | Retrieves string value.                                               | `reader.getString("app.name")`                                               |
| `getString(String key, String defaultValue)`         | Retrieves string or returns default.                                  | `reader.getString("app.name", "Default")`                                    |
| `getInt(String key)`                                 | Retrieves integer value.                                              | `reader.getInt("port")`                                                      |
| `getInt(String key, int defaultValue)`               | Retrieves integer or returns default.                                 | `reader.getInt("port", 8080)`                                                |
| `getBoolean(String key)`                             | Retrieves boolean value.                                              | `reader.getBoolean("enabled")`                                               |
| `getBoolean(String key, boolean defaultValue)`       | Retrieves boolean or returns default.                                 | `reader.getBoolean("enabled", false)`                                        |
| `getAllConfigAs(Class<T> type)`                      | Converts the entire configuration to a POJO of specified type.        | `reader.getAllConfigAs(AppConfig.class)`                                     |
| `getFloat(String key)`                               | Retrieves float value.                                                | `reader.getFloat("piValue")`                                                 |
| `getFloat(String key, float defaultValue)`           | Retrieves float or returns default.                                   | `reader.getFloat("piValue", 3.14f)`                                          |
| `getDouble(String key)`                              | Retrieves double value.                                               | `reader.getDouble("piValue")`                                                |
| `getDouble(String key, double defaultValue)`         | Retrieves double or returns default.                                  | `reader.getDouble("piValue", 3.1415)`                                        |
| `getStringArray(String key)`                         | Retrieves string array.                                               | `reader.getStringArray("hosts")`                                             |
| `getStringArray(String key, String[] defaultValue)`  | Retrieves string array or returns default.                            | `reader.getStringArray("hosts", new String[]{"localhost"})`                  |
| `getStringList(String key)`                          | Retrieves list of strings.                                            | `reader.getStringList("hosts")`                                              |
| `getStringList(String key, List<String> defaultVal)` | Retrieves list of strings or default.                                 | `reader.getStringList("hosts", List.of("localhost"))`                        |
| `getList(String key, Class<E> clazz)`                | Retrieves list of values of specified type.                           | `reader.getList("ports", Integer.class)`                                     |
| `getList(String key, Class<E> clazz, List<E> def)`   | Retrieves list of values or returns default.                          | `reader.getList("ports", Integer.class, List.of(80, 443))`                   |
| `getMap(String key, Class<V> clazz)`                 | Retrieves map of values of specified type.                            | `reader.getMap("db.settings", String.class)`                                 |
| `getMap(String key, Class<V> clazz, Map<V> def)`     | Retrieves map of values or returns default.                           | `reader.getMap("db.settings", String.class, Map.of("timeout", "30"))`        |
| `hasKey(String key)`                                 | Checks if key exists.                                                 | `reader.hasKey("app.name")`                                                  |
| `getAllConfig()`                                     | Retrieves all configuration as a Map.                                 | `reader.getAllConfig()`                                                      |

---

## Exceptions

| Method                                       | Exception                                     | Description                                               |
| -------------------------------------------- | --------------------------------------------- | --------------------------------------------------------- |
| `ConfigFactory.load()`                       | `ConfigException`, `IllegalArgumentException` | Thrown if resource location is invalid or loading fails.  |
| `ConfigReader.getOrThrow(String, Class)`     | `MissingConfigException`                      | Thrown when the requested key does not exist.             |
| `ConfigReader.getOrThrow(String, TypeOf)`    | `MissingConfigException`                      | Thrown when the requested key does not exist.             |
| `ConfigReader.getAllConfigAs(Class)`         | `NullPointerException`                        | Thrown if provided class is null.                         |
| `ConfigReader.get(String, Class)`            | `NullPointerException`, `IllegalArgumentException` | Thrown if key is null/blank or class is null.              |
| `ConfigReader.get(String, TypeOf)`           | `NullPointerException`, `IllegalArgumentException` | Thrown if key or TypeOf is null/blank.                    |
| `ConfigReader.getList()` / `getMap()`        | `NullPointerException`, `IllegalArgumentException` | Thrown if key is null/blank or class is null.              |
| `ConfigReader.hasKey(String)`                | `NullPointerException`, `IllegalArgumentException` | Thrown if key is null or blank.                           |
| All "required key" getters (without default) | `MissingConfigException`                      | Thrown if key does not exist and no default provided.     |

---

## Logging Example

```plaintext
Configuration load completed: loaded=2, failed=1 (total=3)
Loaded: application.properties, application-dev.yaml
Failed: missing-config.yaml (not found)
```

---

## Advanced Features

- Supports **YAML, Properties, JSON, XML formats only.**
- Placeholder substitution: `${profile}` or **any custom variable defined by the user** using `addVariables`. Not limited to profile.
- Ordered overrides (later files override earlier ones)
- Mixed format loading (e.g., YAML + JSON + Properties together)
- Conversion to POJO objects or generic structures via `TypeOf<T>` or `getAllConfigAs(Class<T>)`
- Immutable and cached after initial load

---

## Contributing

1. Fork this repo
2. Create your branch (`feature/my-feature`)
3. Commit and push (`git commit -m 'Add feature'`)
4. Open Pull Request

---

## License

Apache License 2.0

---

## Author

Leonardo R.

---

> "Liana: Configuration that adapts to you, not the other way around."

