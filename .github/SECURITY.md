# Security Policy

## Supported Versions

We release patches for security vulnerabilities for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.x.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability within b58uuid-java, please send an email to the maintainers instead of using the public issue tracker.

**Please do not report security vulnerabilities through public GitHub issues.**

### What to Include

When reporting a vulnerability, please include:

- Type of vulnerability
- Full paths of source file(s) related to the vulnerability
- Location of the affected source code (tag/branch/commit or direct URL)
- Step-by-step instructions to reproduce the issue
- Proof-of-concept or exploit code (if possible)
- Impact of the vulnerability, including how an attacker might exploit it

### Response Timeline

- We will acknowledge receipt of your vulnerability report within 48 hours
- We will provide a detailed response within 7 days
- We will work on a fix and keep you informed of progress
- Once the vulnerability is fixed, we will publicly disclose it

## Security Best Practices

When using b58uuid-java:

1. Always validate input UUIDs before encoding
2. Handle errors appropriately - catch and handle B58UUIDException
3. Keep your Java version up to date
4. Regularly update to the latest version of b58uuid-java

## Security Updates

Security updates will be released as patch versions (e.g., 1.0.1) and announced through:

- GitHub Security Advisories
- Release notes
- Git tags

Thank you for helping keep b58uuid-java and its users safe!
