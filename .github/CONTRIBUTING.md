# Contributing to b58uuid-java

Thank you for your interest in contributing to b58uuid-java! We welcome contributions from the community.

## Development Environment Requirements

### Required Software

- **Java Development Kit (JDK)**: Java 8 or higher
  - Recommended: Java 11, 17, or 21 (LTS versions)
  - Download: [Eclipse Temurin](https://adoptium.net/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
- **Apache Maven**: 3.6.0 or higher
  - Download: [Maven](https://maven.apache.org/download.cgi)
  - Or use your package manager: `brew install maven` (macOS), `apt install maven` (Ubuntu)
- **Git**: For version control
  - Download: [Git](https://git-scm.com/downloads)

### Recommended Tools

- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Code formatter**: Google Java Format or built-in IDE formatter

### Verify Your Setup

```bash
# Check Java version (should be 8 or higher)
java -version

# Check Maven version (should be 3.6.0 or higher)
mvn -version

# Check Git version
git --version
```

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for everyone.

## How to Contribute

### Reporting Bugs

Before creating a bug report, please check existing issues to avoid duplicates. When creating a bug report, include:

- A clear and descriptive title
- Steps to reproduce the issue
- Expected behavior
- Actual behavior
- Java version and OS
- Code example demonstrating the issue

### Suggesting Features

Feature requests are welcome! Please provide:

- A clear description of the feature
- Use case and motivation
- Proposed API or implementation (if applicable)
- Any alternatives you've considered

### Pull Requests

1. **Fork the repository** and create your branch from `main`
2. **Make your changes** following our coding standards
3. **Add tests** for any new functionality
4. **Ensure all tests pass**: `mvn test`
5. **Run code quality checks**:
   ```bash
   mvn spotbugs:check
   mvn checkstyle:check
   ```
6. **Update documentation** if needed
7. **Submit a pull request** with a clear description

## Development Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/b58uuid-java.git
cd b58uuid-java

# Build the project
mvn clean compile

# Run tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Check code quality
mvn spotbugs:check

# Package the library
mvn package
```

## Coding Standards

### Java Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Write clear, self-documenting code
- Add Javadoc comments for public classes and methods
- Keep methods small and focused (ideally under 50 lines)
- Use Java 8 features conservatively for compatibility

### Code Formatting

```bash
# The project uses Google Java Format style
# Configure your IDE to use Google Java Format or run:
mvn checkstyle:check
```

### Testing

- Write tests for all new functionality
- Maintain or improve test coverage (currently >90%)
- Use JUnit 5 for unit tests
- Test edge cases and error conditions
- Ensure tests are deterministic and fast
- Use descriptive test method names

Example test:
```java
@Test
@DisplayName("Should encode UUID to 22-character Base58 string")
void testEncodeUUID() throws B58UUIDException {
    String uuid = "550e8400-e29b-41d4-a716-446655440000";
    String encoded = B58UUID.encodeUUID(uuid);
    
    assertEquals(22, encoded.length());
    assertEquals("BWBeN28Vb7cMEx7Ym8AUzs", encoded);
}
```

### Commit Messages

- Use clear and descriptive commit messages
- Start with a verb in present tense (e.g., "Add", "Fix", "Update")
- Keep the first line under 72 characters
- Add detailed description if needed

Example:
```
Add support for custom Base58 alphabets

- Implement configurable alphabet option
- Add tests for custom alphabets
- Update documentation with examples
```

## Project Structure

```
b58uuid-java/
├── src/
│   ├── main/java/io/b58uuid/
│   │   ├── B58UUID.java           # Main API
│   │   └── B58UUIDException.java  # Exception class
│   └── test/java/io/b58uuid/
│       ├── B58UUIDTest.java       # Main tests (129 tests)
│       └── B58UUIDBenchmarkTest.java  # JMH benchmarks
├── .github/
│   ├── workflows/           # CI/CD workflows
│   └── ISSUE_TEMPLATE/      # Issue templates
├── pom.xml                  # Maven configuration
├── LICENSE                  # MIT License
├── README.md               # User documentation
└── CHANGELOG.md            # Version history
```

## Testing Guidelines

### Unit Tests

- Test all public methods
- Test error conditions and exceptions
- Test edge cases (empty strings, null values, overflow)
- Use parameterized tests for multiple test cases
- Aim for >90% code coverage

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=B58UUIDTest

# Run specific test method
mvn test -Dtest=B58UUIDTest#testEncodeUUID

# Run tests with coverage report
mvn test jacoco:report
# View report: target/site/jacoco/index.html

# Run tests on multiple Java versions (if installed)
JAVA_HOME=/path/to/java8 mvn test
JAVA_HOME=/path/to/java11 mvn test
JAVA_HOME=/path/to/java17 mvn test
```

### Benchmarks

When adding performance-critical code, include JMH benchmarks:

```java
@Benchmark
public String benchmarkEncode() throws B58UUIDException {
    return B58UUID.encode(TEST_BYTES);
}
```

Run benchmarks:
```bash
mvn test-compile exec:java -Dexec.mainClass="io.b58uuid.B58UUIDBenchmarkTest" -Dexec.classpathScope=test
```

## Code Quality Checks

### Static Analysis

```bash
# Run SpotBugs (finds potential bugs)
mvn spotbugs:check

# Run Checkstyle (code style)
mvn checkstyle:check

# View SpotBugs report
mvn spotbugs:gui
```

### Code Coverage

```bash
# Generate coverage report
mvn test jacoco:report

# View report in browser
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target/site/jacoco/index.html  # Windows
```

## Documentation

- Update README.md for user-facing changes
- Add Javadoc comments for public classes and methods
- Include code examples in documentation
- Keep documentation clear and concise
- Update CHANGELOG.md for notable changes

### Javadoc Guidelines

```java
/**
 * Encodes a UUID string to Base58 format.
 * 
 * @param uuidStr A UUID string in standard format (with or without hyphens)
 * @return The Base58-encoded UUID (exactly 22 characters)
 * @throws B58UUIDException if the input string is not a valid UUID
 */
public static String encodeUUID(String uuidStr) throws B58UUIDException {
    // implementation
}
```

## Continuous Integration

The project uses GitHub Actions for CI/CD:

- Tests run on Ubuntu, macOS, and Windows
- Tests run on Java 8, 11, 17, and 21
- Code quality checks run on Java 11
- All checks must pass before merging

## Release Process

Releases are managed by maintainers:

1. Update version in `pom.xml`
2. Update `CHANGELOG.md` with release notes
3. Create a git tag: `git tag v1.x.x`
4. Push tag: `git push origin v1.x.x`
5. GitHub Actions will run tests
6. Create GitHub release with changelog
7. Publish to Maven Central (maintainers only)

## Common Issues

### Java Version Mismatch

If you see "Unsupported class file major version" errors:
- Ensure you're using Java 8 or higher
- Check: `java -version`
- Set JAVA_HOME if needed

### Maven Build Fails

```bash
# Clean and rebuild
mvn clean install

# Skip tests temporarily
mvn clean install -DskipTests

# Update dependencies
mvn clean install -U
```

### Tests Fail Locally

```bash
# Ensure clean state
mvn clean test

# Check Java version
java -version

# Verify Maven version
mvn -version
```

## Questions?

If you have questions about contributing, feel free to:

- Open a discussion on GitHub
- Create an issue with the "question" label
- Check existing issues and discussions

## License

By contributing to b58uuid-java, you agree that your contributions will be licensed under the MIT License.

Thank you for contributing! 🎉
