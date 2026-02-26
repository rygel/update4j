# Update4j Documentation

Welcome to the update4j documentation! This page will help you get started and find the information you need.

## What is Update4j?

Update4j is a lightweight Java library for auto-updating and launching desktop applications. It's designed for Java 9+ and provides:

- **File synchronization** - Download only changed files
- **Flexible launching** - Run from classpath or module path
- **Security** - Sign and verify configurations
- **Customization** - Hook into every step of the update process

## Quick Links

| Guide | Description |
|-------|-------------|
| [Getting Started](USAGE.md#quick-start) | 5-minute quick start guide |
| [Configuration](USAGE.md#configuration) | XML and programmatic configuration |
| [Bootstrap](USAGE.md#bootstrap) | Using the command-line bootstrap |
| [Custom Services](USAGE.md#custom-services) | Custom UpdateHandler and Launcher |
| [Dependency Injection](USAGE.md#dependency-injection) | Using @InjectTarget and @InjectSource |
| [Security](USAGE.md#security) | Signing and verifying configurations |
| [Archive Updates](USAGE.md#archive-based-updates) | Using ZIP archives for updates |
| [Error Handling](USAGE.md#error-handling) | Handling update failures |
| [Advanced Usage](USAGE.md#advanced-usage) | Multiple classloaders, custom streams |

## Installation

### Maven

```xml
<dependency>
    <groupId>org.update4j</groupId>
    <artifactId>update4j</artifactId>
    <version>1.5.9</version>
</dependency>
```

### Gradle

```groovy
implementation 'org.update4j:update4j:1.5.9'
```

### Manual Download

Download from [Maven Central](https://repo1.maven.org/maven2/org/update4j/update4j/1.5.9/update4j-1.5.9.jar)

## Minimal Example

### 1. Create Configuration File (config.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="baseUri" value="https://example.com/updates/"/>
    <property name="default.launcher.main.class" value="com.example.MyApp"/>
    
    <files>
        <file path="lib/app.jar"
              uri="app.jar"
              size="12345"
              checksum="abc123..."/>
    </files>
</configuration>
```

### 2. Create Your Application

```java
package com.example;

import org.update4j.*;
import java.nio.file.*;

public class MyApp {
    public static void main(String[] args) throws Exception {
        // Load configuration
        Configuration config = Configuration.read(
            Files.newBufferedReader(Paths.get("config.xml"))
        );

        // Check for updates and download if needed
        if (config.requiresUpdate()) {
            config.update();
        }

        // Launch the application
        config.launch();
    }
}
```

### 3. Run

```bash
java -jar myapp.jar
```

## Common Use Cases

### Use Case 1: Simple Auto-Update

The most basic use case - check for updates on startup and download if needed.

See: [Quick Start Guide](USAGE.md#basic-update-and-launch)

### Use Case 2: Archive-Based Update

Download all files as a ZIP archive, then install in one step.

See: [Archive-Based Updates](USAGE.md#archive-based-updates)

### Use Case 3: Custom Progress UI

Show a progress bar or custom UI during updates.

See: [Custom UpdateHandler](USAGE.md#custom-updatehandler)

### Use Case 4: Secure Updates

Sign your configuration and verify before running.

See: [Security](USAGE.md#security)

### Use Case 5: Command-Line Bootstrap

Use the built-in bootstrap without writing any code.

See: [Bootstrap](USAGE.md#using-default-bootstrap)

## Key Concepts

### Configuration

The Configuration object defines what files to manage and how to launch your app.

- [XML Configuration](USAGE.md#xml-configuration-file)
- [Programmatic Configuration](USAGE.md#programmatic-configuration)
- [File Metadata](USAGE.md#file-metadata-options)
- [OS-Specific Files](USAGE.md#os-specific-files)

### Bootstrap

The bootstrap is the entry point that handles updating and launching.

- [Default Bootstrap](USAGE.md#using-default-bootstrap)
- [Bootstrap Options](USAGE.md#bootstrap-options)
- [Delegate Pattern](USAGE.md#custom-services)

### Services

Services are pluggable components that customize behavior.

- [UpdateHandler](USAGE.md#custom-updatehandler) - Progress callbacks, custom download logic
- [Launcher](USAGE.md#custom-launcher) - Custom launch logic
- [Dependency Injection](USAGE.md#dependency-injection)

### Security

Keep your updates secure with signing and verification.

- [Signing Configurations](USAGE.md#signing-configuration)
- [Verifying Configurations](USAGE.md#verifying-configuration)
- [Certificate Usage](USAGE.md#using-certificate-file)

## Troubleshooting

### Problem: Files won't download

**Symptoms**: No files are downloaded even though they're newer on the server.

**Solutions**:
1. Check that `baseUri` is correct
2. Verify checksum in your configuration matches the server
3. Check network connectivity

### Problem: Application won't launch

**Symptoms**: App starts but immediately exits or throws exception.

**Solutions**:
1. Verify `default.launcher.main.class` is correct
2. Check that all required JARs are in the configuration
3. Look at logs for class not found errors

### Problem: Update fails with checksum error

**Symptoms**: "Checksum does not match" error during download.

**Solutions**:
1. Regenerate checksum: `sha256sum myfile.jar`
2. Update the checksum in your configuration
3. Check if file was corrupted during upload

### Problem: SecurityException when loading classes

**Symptoms**: `SecurityException` or `AccessControlException` during launch.

**Solutions**:
1. Check module declarations in your JARs
2. Use `--add-opens` or `--add-exports` if needed
3. See [AddPackage](src/main/java/org/update4j/AddPackage.java) for JPMS options

## Best Practices

### 1. Always Sign Your Configuration

```java
Configuration signedConfig = Configuration.builder()
    .baseUri("https://example.com/")
    .file(FileMetadata.readFromPath(Paths.get("app.jar")))
    .sign(privateKey)  // Sign with your private key
    .build();
```

### 2. Use Checksums

Always include checksums in your configuration for verification:

```bash
# Generate checksum
sha256sum myapp.jar
```

```xml
<file path="myapp.jar" 
      uri="myapp.jar"
      checksum="abc123..."/>
```

### 3. Handle Errors Gracefully

```java
UpdateResult result = config.update();
if (result.getException() != null) {
    // Handle error - maybe rollback or notify user
    System.err.println("Update failed: " + result.getException().getMessage());
}
```

### 4. Use Temp Updates for Critical Apps

For applications where interruption is costly:

```java
Path tempDir = Paths.get("update");
config.updateTemp(tempDir);
// On next startup:
if (Update.containsUpdate(tempDir)) {
    Update.finalizeUpdate(tempDir);
}
```

### 5. Test Your Update Flow

Always test your update flow:
1. First run - should download all files
2. Second run - should skip (no updates needed)
3. Modify a file on server - should update only that file
4. Break the network - should handle gracefully

## API Reference

For detailed API documentation, see the [JavaDoc](https://s3.amazonaws.com/docs.update4j.org/javadoc/update4j/index.html).

## Examples

Check out these example projects:

- **Basic Example** - Simple update and launch
- **JavaFX Example** - Using with JavaFX applications
- **Custom Bootstrap** - Building your own bootstrap
- **Delegate Pattern** - Stacking multiple bootstraps

## Getting Help

- [GitHub Discussions](https://github.com/rygel/update4j/discussions)
- [Report Issues](https://github.com/rygel/update4j/issues)
- [Wiki](https://github.com/update4j/update4j/wiki)

---

For more detailed information, see the [Usage Guide](USAGE.md).
