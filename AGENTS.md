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

#### Keep System.out/err for Console Output (No Logging Framework)
- **Location**: `DefaultBootstrap.java:357, 446`, `DefaultLauncher.java:139`, `DefaultUpdateHandler.java:141`
- **Decision**: Keep using System.out/System.err for console output
- **Reason**: Minimizes external dependencies and keeps the library lightweight
- **Impact**: Users cannot control log levels, but this is intentional for simplicity
- **Note**: Do NOT introduce SLF4J or other logging frameworks

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
- **Tested on**: Java 11, 17, 21, 25 (LTS releases)
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
3. ✅ Add tests for zero-coverage classes (COMPLETED in PR #2)
4. Improve error messages and exception handling

### Phase 2: High Priority
5. Replace System.out/err with proper logging framework
6. Add security validations and warnings
7. Refactor unchecked cast warnings
8. Add integration tests

### Phase 3: Medium Priority
9. Improve documentation with examples - ✅ IMPLEMENTED
10. Add delta update functionality - ✅ IMPLEMENTED (checksum-based, only changed files downloaded) - Note: This feature is experimental
11. Add rollback support
12. Enhance debug logging - ✅ IMPLEMENTED (--debug flag)

### Phase 4: Low Priority (SKIPPED - maintain JDK 9 compatibility)
13. Modernization with Java 17+ features (skipped - maintain JDK 9 compatibility)
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

## 📝 Documentation Examples

### 1. Getting Started Guide

#### Basic Configuration and Update

```java
import org.update4j.*;
import java.nio.file.*;

public class MyApp {
    public static void main(String[] args) throws Exception {
        // Create a configuration programmatically
        Configuration config = Configuration.builder()
            .baseUri("https://example.com/updates/")
            .basePath(Paths.get("lib"))
            .file(FileMetadata.readFromPath(
                Paths.get("myapp.jar"),
                UriType.CLASSpath))
            .property("app.version", "1.0.0")
            .build();

        // Check if update is needed
        if (config.requiresUpdate()) {
            System.out.println("Update available!");
            config.update();
        }

        // Launch the application
        config.launch();
    }
}
```

#### Using the Default Bootstrap

```java
// Command line:
// java -jar mybootstrap.jar --local config.xml

// In config.xml:
/*
<configuration>
    <property name="app.version" value="1.0.0"/>
    <property name="default.launcher.main.class" value="com.example.MyApp"/>
    <files>
        <file path="lib/myapp.jar" 
              uri="https://example.com/updates/myapp.jar"
              size="12345"
              checksum="abc123..."/>
    </files>
</configuration>
*/
```

### 2. Custom Services

#### Custom UpdateHandler

```java
import org.update4j.*;
import org.update4j.service.*;

public class MyUpdateHandler implements UpdateHandler {
    @Override
    public void init(UpdateContext context) {
        System.out.println("Starting update...");
    }

    @Override
    public void startCheckUpdates() {
        System.out.println("Checking for updates...");
    }

    @Override
    public void updateCheckUpdatesProgress(float frac) {
        System.out.printf("Check progress: %.0f%%%n", frac * 100);
    }

    @Override
    public void startDownloads() {
        System.out.println("Starting downloads...");
    }

    @Override
    public void updateDownloadProgress(float frac) {
        System.out.printf("Download progress: %.0f%%%n", frac * 100);
    }

    @Override
    public void doneDownloads() {
        System.out.println("All files downloaded!");
    }

    @Override
    public void succeeded() {
        System.out.println("Update succeeded!");
    }

    @Override
    public void failed(Throwable t) {
        System.err.println("Update failed: " + t.getMessage());
    }

    @Override
    public long version() {
        return 1L; // Higher version = preferred
    }
}
```

Register in `META-INF/services/org.update4j.service.UpdateHandler`:
```
com.example.MyUpdateHandler
```

#### Custom Launcher

```java
import org.update4j.*;
import org.update4j.service.*;

public class MyLauncher implements Launcher {
    @Override
    public void launch(LaunchContext context) throws Throwable {
        ClassLoader cl = context.getClassLoader();
        String mainClass = context.getMainClass();
        
        System.out.println("Launching " + mainClass);
        
        // Custom launch logic
        Thread.currentThread().setContextClassLoader(cl);
        Class<?> clazz = Class.forName(mainClass, true, cl);
        clazz.getMethod("main", String[].class)
             .invoke(null, (Object) context.getArgs());
    }

    @Override
    public long version() {
        return 1L;
    }
}
```

### 3. Dependency Injection Examples

#### Using @InjectTarget

```java
import org.update4j.*;
import org.update4j.inject.*;

public class MyService implements UpdateHandler, Injectable {
    
    @InjectTarget(key = "app.version")
    private String appVersion;
    
    @InjectTarget(key = "server.url")
    private String serverUrl;
    
    @InjectSource
    private Configuration config;
    
    @Override
    public void injected() {
        // Called after injection is complete
        System.out.println("App version: " + appVersion);
        System.out.println("Server: " + serverUrl);
    }
    
    @Override
    public void init(UpdateContext context) {
        // Fields are already injected here
        System.out.println("Starting update for version " + appVersion);
    }
    
    @Override
    public long version() {
        return 1L;
    }
}
```

#### Using Injectable Callbacks

```java
public class MyHandler implements UpdateHandler, Injectable {
    
    @InjectSource
    private Configuration config;
    
    private String customProperty;
    
    // Called after @InjectSource and @InjectTarget fields are populated
    @Override
    public void injected() {
        customProperty = config.getProperty("custom.key");
        System.out.println("Injected! Custom property: " + customProperty);
    }
    
    @Override
    public void succeeded() {
        // Use injected values after successful update
    }
}
```

### 4. Security Examples

#### Signing Configuration

```java
import org.update4j.*;
import java.security.*;

KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

// Sign the configuration
Configuration signedConfig = Configuration.builder()
    .baseUri("https://example.com/")
    .file(FileMetadata.readFromPath(Paths.get("app.jar")))
    .sign(keyPair.getPrivate())  // Sign with private key
    .build();

// Write to file
try (Writer out = Files.newBufferedWriter(Paths.get("config.xml"))) {
    signedConfig.write(out);
}
```

#### Verifying Configuration

```java
import org.update4j.*;

// Load and verify configuration
Configuration config = Configuration.read(
    Files.newBufferedReader(Paths.get("config.xml")),
    publicKey  // Your trusted public key
);

// Or verify manually
config.verifyConfiguration(publicKey);
```

#### Using Certificate File

```bash
# Command line with certificate
java -jar bootstrap.jar --remote https://example.com/config.xml --cert certificate.cer
```

```java
// The bootstrap will automatically:
// 1. Load the X.509 certificate
// 2. Validate certificate expiry
// 3. Use public key for signature verification
// 4. Warn if configuration is unsigned
```

---

## 📝 Contributing

When working on issues:
1. Maintain JDK 9 compatibility
2. Add tests for new features
3. Update documentation
4. Keep deprecated code
5. Follow existing code style (formatter.xml)
