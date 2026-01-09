# B58UUID for Java

[![Maven Central](https://img.shields.io/maven-central/v/io.b58uuid/b58uuid.svg)](https://search.maven.org/artifact/io.b58uuid/b58uuid)
[![Javadoc](https://javadoc.io/badge2/io.b58uuid/b58uuid/javadoc.svg)](https://javadoc.io/doc/io.b58uuid/b58uuid)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://github.com/b58uuid/b58uuid-java/workflows/Tests/badge.svg)](https://github.com/b58uuid/b58uuid-java/actions)

Base58-encoded UUID library for Java.

## Why This Library?

- **Compact**: 22 characters instead of 36
- **URL-safe**: No special characters that need escaping
- **Unambiguous**: Uses Bitcoin's Base58 alphabet (excludes 0, O, I, l)
- **Fast**: Optimized encoding/decoding algorithms
- **Safe**: Thread-safe operations with comprehensive error handling
- **Zero dependencies**: Uses only Java standard library
- **Java 8+**: Compatible with Java 8 and higher

## Installation

### Maven

```xml
<dependency>
    <groupId>io.b58uuid</groupId>
    <artifactId>b58uuid</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'io.b58uuid:b58uuid:1.0.0'
```

## Usage

```java
import io.b58uuid.B58UUID;

public class Example {
    public static void main(String[] args) throws Exception {
        // Generate a new UUID
        String b58 = B58UUID.generate();
        System.out.println(b58); // Output: 3FfGK34vwMvVFDedyb2nkf

        // Encode existing UUID
        String encoded = B58UUID.encodeUUID("550e8400-e29b-41d4-a716-446655440000");
        System.out.println(encoded); // Output: BWBeN28Vb7cMEx7Ym8AUzs

        // Decode back to UUID
        String uuid = B58UUID.decodeToUUID("BWBeN28Vb7cMEx7Ym8AUzs");
        System.out.println(uuid); // Output: 550e8400-e29b-41d4-a716-446655440000
    }
}
```

## API

### Methods

- `generate()` - Generate a new random UUID and return Base58 encoding
- `encodeUUID(String uuidStr)` - Encode UUID string to Base58
- `decodeToUUID(String b58Str)` - Decode Base58 string to UUID
- `encode(byte[] data)` - Encode 16-byte UUID to Base58
- `decode(String b58Str)` - Decode Base58 string to 16-byte UUID

### Exceptions

- `B58UUIDException` - Thrown for invalid input or overflow

## Features

- Zero dependencies (uses only Java standard library)
- Always produces exactly 22 characters
- Uses Bitcoin Base58 alphabet (no 0, O, I, l)
- Thread-safe
- Full error handling

## Testing

```bash
mvn test
```

## Development

### Requirements

- Java 8 or higher
- Apache Maven 3.6.0 or higher

### Building from Source

```bash
# Clone the repository
git clone https://github.com/b58uuid/b58uuid-java.git
cd b58uuid-java

# Build and run tests
mvn clean test

# Package the library
mvn package
```

For detailed contribution guidelines, see [CONTRIBUTING.md](.github/CONTRIBUTING.md).

## License

MIT License - see LICENSE file for details.
