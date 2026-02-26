# Update4j - Things to Do

This document outlines potential improvements, bugs to fix, and features to add for the update4j project.

## Important Notes

- **Backward Compatibility**: Deprecated code should be retained to support existing users
- **JDK Version**: Minimum JDK version must remain at 9 to ensure maximum compatibility with users

---

## 🔴 Critical Issues

### 1. Zero Test Coverage Areas (7 classes, 0% coverage)

The following core classes have no test coverage:

- **DefaultBootstrap** - 167 lines, 0%
  - Main bootstrap logic for application lifecycle
  - Handles remote/local config loading
  - Manages update and launch processes

- **DefaultLauncher** - 53 lines, 0%
  - Application launcher with main class execution
  - JavaFX application support
  - Command-line argument handling

- **Injectable** - 43 lines, 0%
  - Dependency injection framework
  - Field exchange between bootstrap and service providers
  - Post-injection callback mechanism

- **AddPackage** - 10 lines, 0%
  - Module add-exports/add-opens configuration
  - JPMS module system integration

- **LaunchContext** - 8 lines, 0%
  - Launch context data structure
  - Configuration and classloader access

- **UnsatisfiedInjectionException** - 4 lines, 0%
  - Error handling for dependency injection failures

- **MapMapper** - 46 lines, 0%
  - Configuration mapping from Map
  - Property resolution

**Recommendation**: Add comprehensive unit tests for these classes to improve overall test coverage from 42% to target 80%+

---

## 🟡 Important Improvements

### 2. Code Quality

#### Replace System.out/err with Logging (3 instances)
- **Location**: `DefaultBootstrap.java:357, 446`, `DefaultLauncher.java:139`
- **Issue**: Direct console output without proper logging framework
- **Impact**: Cannot control log levels or output destinations
- **Solution**: Introduce SLF4J or java.util.logging

#### Refactor Unchecked Cast Warnings (7 instances)
- **Location**: Service.java, UpdateResult.java, UpdateOptions.java, Update.java, DynamicClassLoader.java
- **Issue**: Using `@SuppressWarnings("unchecked")` to suppress type safety warnings
- **Impact**: Potential runtime ClassCastException risks
- **Solution**: Refactor to use type-safe collections or proper generic bounds

#### Improve Error Messages and Exception Handling
- Add more context in error messages
- Include helpful recovery suggestions
- Document edge cases in javadoc

### 3. Security Enhancements

#### Certificate Handling Validation
- **Issue**: Minimal validation checks for certificates
- **Impact**: Potential security vulnerabilities in configuration verification
- **Solution**: Add comprehensive certificate chain validation

#### Security Warnings for Unsigned Configurations
- **Issue**: No warning when loading unsigned configurations
- **Impact**: Users may unknowingly use unverified updates
- **Solution**: Add prominent warnings for unsigned configs

#### Improve Signature Verification Error Messages
- **Issue**: Generic error messages don't help troubleshooting
- **Impact**: Difficult to debug signature verification failures
- **Solution**: Detailed error messages with specific failure reasons

---

## 🟢 Nice-to-Have Features

### 4. Enhanced Testing

#### Integration Tests
- Real-world scenario tests
- End-to-end update and launch flows
- Mock server tests for download operations

#### Platform-Specific Tests
- Windows-specific path handling
- Linux/macOS permission issues
- File lock behavior differences

#### Load Testing
- Concurrent update operations
- Large file downloads
- Multiple simultaneous launches

### 5. Documentation

#### Usage Examples
- Common scenario walkthroughs
- Configuration examples for different use cases
- Migration guides from version to version

#### Edge Case Documentation
- Error handling best practices
- Network failure recovery
- Update conflict resolution

### 6. Developer Experience

#### Better Configuration Validation
- Early error detection during build
- Configuration file validation tools
- IDE support for XML schemas

#### Progress Callbacks
- Fine-grained progress reporting
- Cancellable operations
- Progress estimation improvements

#### Debug Logging
- Verbose logging mode
- Operation tracing
- Performance metrics

### 7. Additional Features

#### Delta Updates
- Only download changed files
- Reduced bandwidth usage
- Faster update process

#### Rollback Functionality
- Automatic rollback on failure
- Manual rollback commands
- Version history tracking

#### Update Scheduling
- Scheduled update checks
- Background updates
- User-configurable update times

#### Configuration Hot-Reload
- Reload without restart
- Validate before applying
- Graceful degradation

#### Custom Update Strategies
- Pluggable update policies
- User-defined update rules
- A/B update testing

---

## 📊 Current Status

### Test Coverage
- **Overall**: 42% (4,830/11,459 instructions)
- **Branches**: 31% (414/1,309)
- **Lines**: 38% (986/2,600)
- **Methods**: 42% (218/509)
- **Classes**: 61% (25/41)

### Java Compatibility
- **Minimum JDK**: 9 (maintain for maximum user compatibility)
- **Tested on**: Java 11, 17, 21 (LTS releases)
- **Recommended**: Java 17 LTS for production

### Codebase Stats
- **Total Java files**: 45
- **Total lines of code**: ~10,838
- **Public API classes**: 22
- **Deprecated methods**: 16 (keep for backward compatibility)

---

## 🎯 Priority Roadmap

### Phase 1: Critical (Do First)
1. ✅ Fix existing failing tests (COMPLETED in PR #1)
2. ✅ Add CI/CD pipelines (COMPLETED in PR #1)
3. Add tests for zero-coverage classes (DefaultBootstrap, DefaultLauncher, Injectable)
4. Improve error messages and exception handling

### Phase 2: High Priority
5. Replace System.out/err with proper logging framework
6. Add security validations and warnings
7. Refactor unchecked cast warnings
8. Add integration tests

### Phase 3: Medium Priority
9. Improve documentation with examples
10. Add delta update functionality
11. Add rollback support
12. Enhance debug logging

### Phase 4: Low Priority
13. Modernization with Java 17+ features (in future major version, keep JDK 9 min)
14. Additional update strategies
15. Performance optimizations
16. Update scheduling

---

## ⚠️ Deprecated Code Policy

**DO NOT REMOVE** deprecated code. These methods are retained for backward compatibility:

- `Update.java` - Entire file (141 lines)
- `Configuration.java` - 13 deprecated update methods (lines 936-1347)
- `UpdateContext.java` - `getTempDirectory()` method (line 109)
- `ConfigImpl.java` - 2 deprecated methods

These were superseded by the new archive-based update mechanism in v1.5.x but remain available for smooth migration. Remove only in a future major version (2.0+) with clear migration guide.

---

## 📝 Contributing

When working on issues:
1. Maintain JDK 9 compatibility
2. Add tests for new features
3. Update documentation
4. Keep deprecated code
5. Follow existing code style (formatter.xml)
