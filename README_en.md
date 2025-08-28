Language: [日本語](./README.md) | English

# Jackson-Databind-Jsonc

This project extends Jackson's `JsonMapper` by adding a `JsoncMapper` to handle JSONC (JSON with Comments).

## Features

- Supports JSONC with block comments (`/* */`) and end-of-line comments (`//`)
- **NEW**: Optional JSON5 features via Builder pattern
  - Single-quoted strings (`'text'` → `"text"`)
  - Hexadecimal numbers (`0xFF` → `255`)
  - Plus sign numbers (`+123` → `123`)
  - Infinity and NaN literals (`Infinity`/`NaN` → `null`)
  - Multiline strings and unescaped control characters
- Extends Jackson's `JsonMapper`
- Multi-version Java support (Java 8, 11, 17, 21, 24)
- Dual distribution strategy for different deployment scenarios
- Protection against ReDoS attacks
- Optional trailing comma removal

## Supported Comment Formats

### ✅ Fully Supported
- **Block comments**: `/* comment */` - multi-line support
- **End-of-line comments**: `// comment` - to end of line
- **Multi-line comments**: comments spanning multiple lines
- **String protection**: comments inside JSON strings are preserved

```javascript
{
    /* Main configuration section */
    "database": {
        "host": "localhost", // Default host
        "port": 5432,
        /* Multi-line comment
           with detailed
           database configuration */
        "timeout": 30
    },
    "message": "This /* is not removed */ from string" // String content protected
}
```

### ❌ Unsupported Features
- **Nested comments**: `/* outer /* inner */ outer */` - not supported
- **Other JSON5 features**: unquoted object keys, etc.

### 🔧 Optional JSON5 Features
All JSON5 features are disabled by default and can be individually enabled via the Builder pattern:

#### Trailing Comma Removal
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowTrailingCommas(true)
    .build();
```

#### Single Quote Strings
Converts single-quoted strings to double-quoted JSON format:
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowSingleQuotes(true)
    .build();

// Input: { 'key': 'value' }
// Output: { "key": "value" }
```

#### Hexadecimal Numbers
Converts hexadecimal literals to decimal format:
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowHexNumbers(true)
    .build();

// Input: { "value": 0xFF }
// Output: { "value": 255 }
```

#### Plus Sign Numbers
Removes explicit plus signs from positive numbers:
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowPlusNumbers(true)
    .build();

// Input: { "value": +123 }
// Output: { "value": 123 }
```

#### Infinity and NaN
Converts JavaScript-style Infinity and NaN to JSON null:
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowInfinityAndNaN(true)
    .build();

// Input: { "inf": Infinity, "nan": NaN }
// Output: { "inf": null, "nan": null }
```

#### Multiline Strings and Control Characters
Additional features for enhanced JSON5 compatibility:
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowMultilineStrings(true)
    .allowUnescapedControlChars(true)
    .build();
```

#### All Features Combined
Enable multiple JSON5 features simultaneously:
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowTrailingCommas(true)
    .allowSingleQuotes(true)
    .allowHexNumbers(true)
    .allowPlusNumbers(true)
    .allowInfinityAndNaN(true)
    .allowMultilineStrings(true)
    .allowUnescapedControlChars(true)
    .build();

// Parse complex JSON5 input
String json5 = """
{
    /* Configuration */
    'name': 'My App',        // Single quotes
    "version": 0xFF,         // Hex number  
    'port': +8080,           // Plus number
    "maxValue": Infinity,    // Infinity
    'enabled': true,         // Trailing comma
}
""";

MyConfig config = mapper.readValue(json5, MyConfig.class);
```

## Requirements

### Java Version Support

This library supports multiple Java versions:

- Java 8 (LTS) – minimum required
- Java 11 (LTS) – fully supported
- Java 17 (LTS) – fully supported
- Java 21 (LTS) – fully supported
- Java 24 – supported (when available)

For maximum compatibility, classes are compiled to Java 8 bytecode (major version 52) and built/tested across all supported Java versions.

### Testing Approach

- Build Testing: Built and tested with Gradle (requires Java 17+)
- Runtime Compatibility: Generated JAR verified on Java 8+
- Bytecode Target: All classes compiled to Java 8 bytecode

## Distribution Options

To meet different environment requirements, two distribution formats are provided.

### Slim JAR (Recommended)
- File: `jackson-databind-jsonc-1.0.0.jar` (~5KB)
- Use Case: Modern Jackson environments, Maven/Gradle projects
- Dependencies: Requires Jackson 2.18.4+
- Benefits: Lightweight, flexible dependency management

### All-in-One JAR (Enterprise)
- File: `jackson-databind-jsonc-1.0.0-all.jar` (~7.8MB)
- Use Case: Enterprise Java apps, legacy environments, dependency conflict avoidance
- Dependencies: Self-contained (Jackson included)
- Benefits: Single-file deployment, no dependency conflicts

## Installation

> **✅ Maven Central Publishing Status**: This project now supports automated publishing using Maven Central Portal. Latest versions are available from Maven Central.

### Maven (Slim JAR)
```xml
<dependency>
    <groupId>jp.vemi</groupId>
    <artifactId>jackson-databind-jsonc</artifactId>
    <version>1.0.5</version>
</dependency>
```

### Gradle (Slim JAR)
```groovy
implementation 'jp.vemi:jackson-databind-jsonc:1.0.5'
```

### Manual Installation
1. Download the JAR from the [releases page](https://github.com/vemic/jackson-databind-jsonc/releases)
2. Add it to your project's classpath

**📋 Release Information:**
- Automated publishing: Available via Maven Central Portal automation
- Manual download: Immediately available from GitHub Releases
- Details: See [PUBLISHING.md](./PUBLISHING.md)

## When to Use Which JAR

### Choose Slim JAR when:
- ✅ Jackson 2.18.x+ is available
- ✅ Dependencies are managed with Maven/Gradle
- ✅ No conflicts with other libraries
- ✅ Minimal JAR size is desired

### Choose All-in-One JAR when:
- ✅ Enterprise Java application environment
- ✅ Jackson versions are fixed
- ✅ You need to avoid dependency conflicts
- ✅ Single JAR deployment is required
- ✅ You want to avoid complex dependency management

## Usage

### Basic Usage (Both JARs)
```java
import jp.vemi.jsoncmapper.JsoncMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Example {
    public static void main(String[] args) {
        // JSONC supports both block and end-of-line comments
        String jsonWithComments = """
            {
                /* Block comment */
                "name": "example", // End-of-line comment
                "value": 42
            }
            """;
        JsoncMapper mapper = new JsoncMapper();
        
        try {
            MyClass obj = mapper.readValue(jsonWithComments, MyClass.class);
            System.out.println(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
```

### Spring Boot Environment (Slim JAR)
```java
import jp.vemi.jsoncmapper.JsoncMapper;

@Component
public class ModernJsoncProcessor {
    private final JsoncMapper mapper = new JsoncMapper();
    
    public <T> T parseJsonc(String jsoncContent, Class<T> valueType) 
            throws JsonProcessingException {
        return mapper.readValue(jsoncContent, valueType);
    }
}
```

### Enterprise Java Application (All-in-One JAR)
```java
import jp.vemi.jsoncmapper.JsoncMapper;

public class IntraMartJsoncProcessor {
    private final JsoncMapper mapper = new JsoncMapper();
    
    public <T> T parseJsonc(String jsoncContent, Class<T> valueType) 
            throws JsonProcessingException {
        return mapper.readValue(jsoncContent, valueType);
    }
}
```

### Reading from Files
```java
import jp.vemi.jsoncmapper.JsoncMapper;
import java.io.File;

JsoncMapper mapper = new JsoncMapper();

// Read directly from file
MyConfig config = mapper.readValue(new File("config.jsonc"), MyConfig.class);

// Read from InputStream
try (InputStream is = new FileInputStream("config.jsonc")) {
    MyConfig config = mapper.readValue(is, MyConfig.class);
}
```

### Using TypeReference for Generic Types
```java
import com.fasterxml.jackson.core.type.TypeReference;

String jsoncArray = """
    [
        /* User list */
        { "name": "Alice", "age": 30 }, // User 1
        { "name": "Bob", "age": 25 }    // User 2
    ]
    """;

List<User> users = mapper.readValue(jsoncArray, new TypeReference<List<User>>() {});
```

### Builder Pattern Configuration
```java
// Enable trailing comma removal
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowTrailingCommas(true)
    .build();

String jsoncWithTrailingCommas = """
    {
        "items": [
            "item1",
            "item2", // Trailing comma will be removed
        ],
        "enabled": true, // Object trailing comma too
    }
    """;

MyClass obj = mapper.readValue(jsoncWithTrailingCommas, MyClass.class);
```

### Flexible Processing with JsonNode
```java
import com.fasterxml.jackson.databind.JsonNode;

String jsoncData = """
    {
        /* Dynamic configuration */
        "settings": {
            "theme": "dark", // UI theme
            "notifications": true
        }
    }
    """;

JsonNode node = mapper.readTree(jsoncData);
String theme = node.get("settings").get("theme").asText(); // "dark"
```

## Performance Considerations

### Normal File Sizes (< 10MB)
- Fast processing capabilities
- Memory usage approximately 1.5x the original file size

### Large Files (> 10MB)
- **Recommended**: Pre-process to remove comments and reduce file size
- **Alternative**: Consider streaming processing

```java
// Recommended pattern for large files
public class LargeFileProcessor {
    public <T> T processLargeJsonc(File largeFile, Class<T> valueType) 
            throws IOException {
        
        // 1. Check file size
        if (largeFile.length() > 10 * 1024 * 1024) { // 10MB
            // 2. Pre-remove comments and save to temporary file
            String content = Files.readString(largeFile.toPath());
            String cleaned = JsoncUtils.removeComments(content);
            
            // 3. Process with standard Jackson
            return new JsonMapper().readValue(cleaned, valueType);
        }
        
        // Normal size files can use JsoncMapper directly
        return new JsoncMapper().readValue(largeFile, valueType);
    }
}
```

### Performance Optimization Tips
- **Slim JAR**: Advantages in startup time and memory usage
- **All-in-One JAR**: Reduced dependency resolution time
- **Builder settings**: Disable unnecessary features

## Security Features

### Protection Against ReDoS Attacks
Linear-time algorithms without regular expressions provide protection against malicious input.

```java
// Safe processing even with malicious input
String maliciousInput = "/*" + "/*".repeat(10000) + "*/";
String result = JsoncUtils.removeComments(maliciousInput); // Processes safely
```

### Input Validation
- **Null input**: Proper error handling with `IllegalArgumentException`
- **Invalid format**: Detailed error information via `JsonProcessingException`
- **String protection**: Comment-like text inside JSON strings is preserved

### Handling Untrusted Input Sources
```java
public class SecureProcessor {
    private final JsoncMapper mapper = new JsoncMapper();
    
    public <T> Optional<T> safeParseJsonc(String untrustedInput, Class<T> valueType) {
        try {
            // Input validation is automatically performed
            T result = mapper.readValue(untrustedInput, valueType);
            return Optional.of(result);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            // Logging and monitoring
            logger.warn("Failed to parse JSONC input: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
```

## Environment-Specific Recommendations

### Modern Spring Boot
- Recommended: Slim JAR
- Reason: Spring Boot manages the latest Jackson

### Traditional Web Application Servers
- Recommended: All-in-One JAR
- Reason: Avoid conflicts with server-side Jackson

### Enterprise Java Environments
- Recommended: All-in-One JAR
- Reason: Enterprise platforms often fix specific Jackson versions

### Microservices
- Recommended: Slim JAR
- Reason: Container size optimization and dependency injection

## Development

### Building and Testing
This project uses Gradle. Java 21 is required to build.

```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run specific test classes
./gradlew test --tests "jp.vemi.jsoncmapper.JsoncUtilsTest"
./gradlew test --tests "jp.vemi.jsoncmapper.SecurityTest"
./gradlew test --tests "jp.vemi.jsoncmapper.JsoncMapperTest"
```

### GitHub Workflows

#### PR Test Execution
A workflow is included to run tests manually or automatically on PR events.

Manual Execution:
1. Go to the Actions tab
2. Select "PR Test Execution"
3. Click "Run workflow"
4. Configure options:
   - Test Scope: `all`, `unit-only`, `security-only`, or `integration-only`
   - Run Build: Enable/disable full build before tests
   - PR Number: Optionally specify a PR

Automatic Execution:
Runs on PR opened/synchronize/ready_for_review events with full scope.

Features:
- Selective test execution
- Detailed results and summaries
- PR comments with results
- Gradle caching for faster runs
- Timeout protection (10 minutes)

## Troubleshooting

### Dependency Conflict Errors
**Symptoms**: `NoSuchMethodError`, `ClassCastException`, etc.

**Solutions**:
1. Switch to All-in-One JAR
```xml
<!-- For Maven -->
<dependency>
    <groupId>jp.vemi</groupId>
    <artifactId>jackson-databind-jsonc</artifactId>
    <version>1.0.0</version>
    <classifier>all</classifier>
</dependency>
```

2. Use exclusion settings to resolve Jackson conflicts
```xml
<dependency>
    <groupId>other-library</groupId>
    <artifactId>with-old-jackson</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### ClassNotFoundException
**Symptoms**: `jp.vemi.jsoncmapper.JsoncMapper` not found

**Solutions**:
1. **Slim JAR**: Verify Jackson 2.18.4+ dependency
2. **All-in-One JAR**: Verify JAR is on the classpath
3. Refresh Maven/Gradle dependencies: `./gradlew --refresh-dependencies`

### Comment Parsing Errors
**Symptoms**: Comments not properly removed or exceptions thrown

**Causes and Solutions**:
1. **Nested comments**: Not supported
```java
// ❌ This won't work
String invalid = "{ /* outer /* inner */ outer */ \"key\": \"value\" }";

// ✅ Use this instead
String valid = "{ /* outer comment */ \"key\": \"value\" /* inner comment */ }";
```

2. **Escaped strings**: Ensure proper escaping
```java
// ✅ Correct approach
String jsonc = "{ \"path\": \"C:\\\\\\\\/* not comment */\\\\\\\\file\" }";
```

### Performance Issues
**Symptoms**: Out of memory or timeout with large files

**Solutions**:
1. **Check file size**: Consider pre-processing for files > 10MB
```java
if (file.length() > 10_000_000) {
    // Pre-remove comments with JsoncUtils
    String cleaned = JsoncUtils.removeComments(content);
    return new JsonMapper().readValue(cleaned, MyClass.class);
}
```

2. **Increase JVM heap**: Use `-Xmx2g` or similar

### Build-Related Issues
**Symptoms**: Build errors or Gradle problems

**Solutions**:
1. **Check Java version**: Building requires Java 21
```bash
java -version # Should show Java 21
export JAVA_HOME=/path/to/java21
```

2. **Clear Gradle cache**:
```bash
./gradlew clean build --refresh-dependencies
```

3. **Runtime environment**: Runs on Java 8+ for execution