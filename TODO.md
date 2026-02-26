# Update4j TODO

## Critical Issues

### Zero Test Coverage Areas

- [ ] Add tests for **DefaultBootstrap** (167 lines, 0% coverage)
  - Main bootstrap logic for application lifecycle
  - Handles remote/local config loading
  - Manages update and launch processes

- [ ] Add tests for **DefaultLauncher** (53 lines, 0% coverage)
  - Application launcher with main class execution
  - JavaFX application support
  - Command-line argument handling

- [ ] Add tests for **Injectable** (43 lines, 0% coverage)
  - Dependency injection framework
  - Field exchange between bootstrap and service providers
  - Post-injection callback mechanism

- [ ] Add tests for **AddPackage** (10 lines, 0% coverage)
  - Module add-exports/add-opens configuration
  - JPMS module system integration

- [ ] Add tests for **LaunchContext** (8 lines, 0% coverage)
  - Launch context data structure
  - Configuration and classloader access

- [ ] Add tests for **UnsatisfiedInjectionException** (4 lines, 0% coverage)
  - Error handling for dependency injection failures

- [ ] Add tests for **MapMapper** (46 lines, 0% coverage)
  - Configuration mapping from Map
  - Property resolution

**Goal**: Improve overall test coverage from 42% to target 80%+

## Code Quality Improvements

- [ ] Replace System.out/err with logging framework
  - Location: `DefaultBootstrap.java:357, 446`, `DefaultLauncher.java:139`
  - Issue: Direct console output without proper logging framework
  - Impact: Cannot control log levels or output destinations
  - Solution: Introduce SLF4J or java.util.logging

- [ ] Refactor unchecked cast warnings (7 instances)
  - Location: Service.java, UpdateResult.java, UpdateOptions.java, Update.java, DynamicClassLoader.java
  - Issue: Using `@SuppressWarnings("unchecked")` to suppress type safety warnings
  - Impact: Potential runtime ClassCastException risks
  - Solution: Refactor to use type-safe collections or proper generic bounds

- [ ] Improve error messages and exception handling
  - [ ] Add more context in error messages
  - [ ] Include helpful recovery suggestions
  - [ ] Document edge cases in javadoc

## Security Enhancements

- [ ] Certificate handling validation
  - Issue: Minimal validation checks for certificates
  - Impact: Potential security vulnerabilities in configuration verification
  - Solution: Add comprehensive certificate chain validation

- [ ] Add security warnings for unsigned configurations
  - Issue: No warning when loading unsigned configurations
  - Impact: Users may unknowingly use unverified updates
  - Solution: Add prominent warnings for unsigned configs

- [ ] Improve signature verification error messages
  - Issue: Generic error messages don't help troubleshooting
  - Impact: Difficult to debug signature verification failures
  - Solution: Detailed error messages with specific failure reasons

## Enhanced Testing

- [ ] Integration tests
  - [ ] Real-world scenario tests
  - [ ] End-to-end update and launch flows
  - [ ] Mock server tests for download operations

- [ ] Platform-specific tests
  - [ ] Windows-specific path handling
  - [ ] Linux/macOS permission issues
  - [ ] File lock behavior differences

- [ ] Load testing
  - [ ] Concurrent update operations
  - [ ] Large file downloads
  - [ ] Multiple simultaneous launches

## Documentation

- [ ] Usage examples
  - [ ] Common scenario walkthroughs
  - [ ] Configuration examples for different use cases
  - [ ] Migration guides from version to version

- [ ] Edge case documentation
  - [ ] Error handling best practices
  - [ ] Network failure recovery
  - [ ] Update conflict resolution

## Developer Experience

- [ ] Better configuration validation
  - [ ] Early error detection during build
  - [ ] Configuration file validation tools
  - [ ] IDE support for XML schemas

- [ ] Progress callbacks
  - [ ] Fine-grained progress reporting
  - [ ] Cancellable operations
  - [ ] Progress estimation improvements

- [ ] Debug logging
  - [ ] Verbose logging mode
  - [ ] Operation tracing
  - [ ] Performance metrics

## Additional Features

- [ ] Delta updates
  - [ ] Only download changed files
  - [ ] Reduced bandwidth usage
  - [ ] Faster update process

- [ ] Rollback functionality
  - [ ] Automatic rollback on failure
  - [ ] Manual rollback commands
  - [ ] Version history tracking

- [ ] Update scheduling
  - [ ] Scheduled update checks
  - [ ] Background updates
  - [ ] User-configurable update times

- [ ] Configuration hot-reload
  - [ ] Reload without restart
  - [ ] Validate before applying
  - [ ] Graceful degradation

- [ ] Custom update strategies
  - [ ] Pluggable update policies
  - [ ] User-defined update rules
  - [ ] A/B update testing

## Performance Optimizations

- [ ] Optimize file download buffering (currently 8KB)
- [ ] Parallel file operations for large updates
- [ ] Cache frequently accessed configurations

## Completed Tasks

- [x] Fix TestFileMetadata test (path issue on Windows)
- [x] Fix TestFull test (OS detection logic)
- [x] Add CI/CD pipeline with multi-platform testing
- [x] Add JaCoCo code coverage plugin
- [x] Add test artifacts upload
- [x] Add Codecov integration
- [x] Add tests for DefaultLauncher (11 test cases, 69% coverage)
- [x] Add tests for LaunchContext (7 test cases, 100% coverage)

## Important Notes

- **Backward Compatibility**: Deprecated code should be retained to support existing users
- **JDK Version**: Minimum JDK version must remain at 9 to ensure maximum compatibility
- **Deprecated Code**: DO NOT REMOVE deprecated code. These methods are retained for backward compatibility:
  - `Update.java` - Entire file (141 lines)
  - `Configuration.java` - 13 deprecated update methods (lines 936-1347)
  - `UpdateContext.java` - `getTempDirectory()` method (line 109)
  - `ConfigImpl.java` - 2 deprecated methods
  - Remove only in a future major version (2.0+) with clear migration guide

## Priority Roadmap

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

## Contributing

When working on issues:
1. Maintain JDK 9 compatibility
2. Add tests for new features
3. Update documentation
4. Keep deprecated code
5. Follow existing code style (formatter.xml)
