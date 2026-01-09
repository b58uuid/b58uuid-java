# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-01-09

### Added
- Initial release of b58uuid for Java
- Fast Base58 encoding/decoding for UUIDs
- Cryptographically secure UUID generation using SecureRandom
- Comprehensive error handling with B58UUIDException
- Support for Java 8 and higher
- Bitcoin Base58 alphabet (excludes 0, O, I, l)
- Always 22-character output for consistent formatting
- Thread-safe operations
- Overflow protection with checked arithmetic
- Full Javadoc documentation
- MIT License

### Features
- `generate()` - Generate random UUID with Base58 encoding
- `encodeUUID()` - Encode UUID string to Base58
- `decodeToUUID()` - Decode Base58 string to UUID
- `encode()` - Encode 16-byte array to Base58
- `decode()` - Decode Base58 string to 16-byte array

### Dependencies
- Zero runtime dependencies
- JUnit 5 for testing (test scope only)

[1.0.0]: https://github.com/b58uuid/b58uuid-java/releases/tag/v1.0.0
