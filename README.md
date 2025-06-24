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

Liana provides essential configuration capabilities designed for flexibility and simplicity in Java applications:

- **Multi-format support**: Load and merge multiple configuration files (YAML, JSON, Properties, XML) seamlessly.
- **Ordered overrides**: Later-loaded files override earlier ones for environment-specific layering.
- **Custom placeholder resolution**: Replace placeholders (e.g., `${profile}`) dynamically.
- **Variable injection**: Inject variables via fluent API or programmatically.
- **Type-safe access**: Retrieve config as `String`, `int`, `boolean`, lists, maps, arrays, or POJOs.
- **POJO and generic mapping**: Deserialize config sections into POJOs or generic structures using `TypeOf<T>`.
- **Complete config snapshot**: Access the full config tree as an unmodifiable `Map<String, Object>` or a full POJO.
- **Thread-safe and immutable**: Config data is immutable after loading.
- **Optional verbose logging**: Detailed logs for resource loading and resolution.

---

## Installation

**Maven:**

```xml
<dependency>
  <groupId>io.github.liana</groupId>
  <artifactId>liana-config</artifactId>
  <version>1.0.0</version>
</dependency>
```

**Gradle:**

```groovy
dependencies {
  implementation 'io.github.liana:liana-config:1.0.0'
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
If you define the configuration like this:

```java
ConfigResourceLocation location = ConfigResourceLocation.builder().build();
```

Liana will apply the following **defaults**:

- Provider: **classpath**
- Profile variable: **profile**
- Default profile: **default**
- Profile environment variable: **LIANA_PROFILE**
- Base resource name: **application**
- Base resource pattern: **application-${profile}**

This means Liana will search the classpath for:

1. A file named `application` (in any supported format).
2. A file matching the pattern `application-${profile}` (with `${profile}` resolved from the environment variable `LIANA_PROFILE`).

If `LIANA_PROFILE` is **not set**, Liana uses the default profile value: **default**.

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
    private String name;

    // Getters and Setters omitted for brevity
}

public class ServerConfig {
    private String host;
    private int port;

    // Getters and Setters omitted for brevity
}
```

### Load and read configuration:

```java
ConfigReader reader = ConfigFactory.load(location);
String appName = reader.getString("app.name", "DefaultApp");
int port = reader.getInt("server.port", 8080);
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

### Overview

Specifies **where and how configuration resources are loaded**. This includes:

- **Resource provider** (e.g., "classpath").
- **Multiple resource files** (mixed formats supported).
- **Variable substitution support**, allowing user-defined placeholders like `${customVar}`.
- **Credential management** (useful for secret or external resources).
- **Verbose logging** for debugging configuration loading order and status.

It also defines the **loading order**, crucial for resolving overrides when multiple resources provide the same key.

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

### Overview

Provides type-safe and flexible methods for accessing configuration values loaded from one or multiple sources. It allows:

- Retrieval of **basic types** (`String`, `int`, `boolean`, etc.).
- Handling of **optional and required keys**, with or without default values.
- Retrieval of **complex and generic types** via `TypeOf<T>`.
- **Conversion of entire configuration to POJO objects** via `getAllConfigAs(Class<T>)`.
- Support for lists (`List<T>`), maps (`Map<String, T>`), and arrays (`String[]`).
- Verification of the existence of keys via `hasKey(String)`.
- Complete configuration snapshot as `Map<String, Object>` via `getAllConfig()`.

This API abstracts any format (YAML, JSON, XML, Properties) behind a simple and fluent Java interface, handling missing keys, type conversion, and defaults gracefully.

| Method                                               | Description                                                           | Example                                                                      |
| ---------------------------------------------------- |-----------------------------------------------------------------------| ---------------------------------------------------------------------------- |
| `get(String key, Class<T> type)`                     | Retrieves optional value for key, converted to specified type.        | `reader.get("app.name", String.class)`                                       |
| `get(String key, TypeOf<T> typeOf)`                  | Retrieves optional value for key, converted to complex/generic type.  | `reader.get("servers", new TypeOf<List<String>>() {})`                       |
| `get(String key, Class<T> type, T defaultValue)`     | Retrieves value or returns default if missing.                        | `reader.get("timeout", Integer.class, 30)`                                   |
| `get(String key, TypeOf<T> typeOf, T defaultValue)`  | Retrieves value for key (generic type) or returns default if missing. | `reader.get("servers", new TypeOf<List<String>>() {}, List.of("localhost"))` |
| `getString(String key)`                              | Retrieves string value.                                               | `reader.getString("app.name")`                                               |
| `getString(String key, String defaultValue)`         | Retrieves string or returns default.                                  | `reader.getString("app.name", "Default")`                                    |
| `getInt(String key)`                                 | Retrieves integer value.                                              | `reader.getInt("port")`                                                      |
| `getInt(String key, int defaultValue)`               | Retrieves integer or returns default.                                 | `reader.getInt("port", 8080)`                                                |
| `getBoolean(String key)`                             | Retrieves boolean value.                                              | `reader.getBoolean("enabled")`                                               |
| `getBoolean(String key, boolean defaultValue)`       | Retrieves boolean or returns default.                                 | `reader.getBoolean("enabled", false)`                                        |
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
| `getAllConfigAs(Class<T> type)`                      | Converts the entire configuration to specified type.                  | `reader.getAllConfigAs(AppConfig.class)`                                     |

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
