# Update4j Usage Guide

This guide provides practical examples for using update4j in your applications.

## Table of Contents
1. [Quick Start](#quick-start)
2. [Configuration](#configuration)
3. [Bootstrap](#bootstrap)
4. [Custom Services](#custom-services)
5. [Dependency Injection](#dependency-injection)
6. [Security](#security)

---

## Quick Start

### Basic Update and Launch

```java
import org.update4j.*;
import java.nio.file.*;

public class MyApp {
    public static void main(String[] args) throws Exception {
        // Load configuration from XML file
        Configuration config = Configuration.read(
            Files.newBufferedReader(Paths.get("config.xml"))
        );

        // Check if update is needed
        if (config.requiresUpdate()) {
            System.out.println("Update available! Downloading...");
            config.update();
        }

        // Launch the application
        config.launch();
    }
}
```

### Programmatic Configuration

```java
Configuration config = Configuration.builder()
    .baseUri("https://example.com/updates/")
    .basePath(Paths.get("lib"))
    .file(FileMetadata.readFromPath(
        Paths.get("myapp.jar"),
        UriType.CLASSPATH))
    .property("app.version", "1.0.0")
    .build();
```

---

## Configuration

### XML Configuration File

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Base URI for all files -->
    <property name="baseUri" value="https://example.com/updates/"/>
    
    <!-- Application properties -->
    <property name="app.name" value="MyApp"/>
    <property name="app.version" value="1.0.0"/>
    
    <!-- Main class for launcher -->
    <property name="default.launcher.main.class" value="com.example.MyApp"/>
    
    <!-- Files to manage -->
    <files>
        <file path="lib/myapp.jar"
              uri="myapp.jar"
              size="12345"
              checksum="abc123..."/>
        <file path="lib/lib1.jar"
              uri="lib/lib1.jar"/>
    </files>
</configuration>
```

### File Metadata Options

```java
FileMetadata file = FileMetadata.readFromPath(Paths.get("myapp.jar"))
    .uri("https://example.com/updates/myapp.jar")  // Override URI
    .os(OS.WINDOWS)                                // Windows only
    .arch("x64")                                   // 64-bit only
    .ignoreBootConflict();                          // Don't check boot conflicts
```

### OS-Specific Files

```java
Configuration config = Configuration.builder()
    .baseUri("https://example.com/")
    .basePath(Paths.get("lib"))
    .file(FileMetadata.readFromPath(Paths.get("app.jar")))
    .file(FileMetadata.readFromPath(Paths.get("app-windows.jar")).os(OS.WINDOWS))
    .file(FileMetadata.readFromPath(Paths.get("app-linux.jar")).os(OS.LINUX))
    .file(FileMetadata.readFromPath(Paths.get("app-mac.jar")).os(OS.MAC))
    .build();
```

---

## Bootstrap

### Using Default Bootstrap

The default bootstrap can be run from command line:

```bash
# Local configuration only
java -jar bootstrap.jar --local config.xml

# Remote configuration
java -jar bootstrap.jar --remote https://example.com/config.xml

# Update first, then launch
java -jar bootstrap.jar --remote https://example.com/config.xml --update

# Launch first, then check for updates in background
java -jar bootstrap.jar --local config.xml --launchFirst
```

### Bootstrap Options

| Option | Description |
|--------|-------------|
| `--local <path>` | Load configuration from local file |
| `--remote <url>` | Load configuration from remote URL |
| `--update` | Perform update before launching |
| `--launchFirst` | Launch existing app, then check for updates |
| `--syncLocal` | Sync remote config to local file |
| `--cert <path>` | Path to X.509 certificate for signature verification |
| `--delegate <class>` | Delegate class to handle lifecycle |
| `--debug` | Enable debug output for troubleshooting |

---

## Custom Services

### Custom UpdateHandler

```java
import org.update4j.*;
import org.update4j.service.*;

public class MyUpdateHandler implements UpdateHandler {
    
    @Override
    public void init(UpdateContext context) {
        System.out.println("Initializing update...");
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
        System.out.println("Update completed successfully!");
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

### Custom Launcher

```java
import org.update4j.*;
import org.update4j.service.*;

public class MyLauncher implements Launcher {
    
    @Override
    public void launch(LaunchContext context) throws Throwable {
        ClassLoader cl = context.getClassLoader();
        String mainClass = context.getMainClass();
        
        System.out.println("Launching: " + mainClass);
        
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

Register in `META-INF/services/org.update4j.service.Launcher`:
```
com.example.MyLauncher
```

---

## Dependency Injection

### InjectTarget - Receiving Values

```java
import org.update4j.*;
import org.update4j.inject.*;

public class MyHandler implements UpdateHandler, Injectable {
    
    // Inject property values
    @InjectTarget(key = "app.version")
    private String appVersion;
    
    @InjectTarget(key = "server.url")
    private String serverUrl;
    
    // Inject the Configuration object
    @InjectSource
    private Configuration config;
    
    // Called after all injections are complete
    @Override
    public void injected() {
        System.out.println("Injected! App version: " + appVersion);
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

### Using with Configuration.update()

```java
MyHandler handler = new MyHandler();
config.update(handler);  // Handler gets injected before update starts
```

### Injectable Callbacks

```java
public class ProgressHandler implements UpdateHandler, Injectable {
    
    @InjectSource
    private Configuration config;
    
    private int downloadCount;
    
    @Override
    public void injected() {
        // Called after @InjectSource fields are populated
        System.out.println("Configuration loaded: " + config.getProperty("app.name"));
    }
    
    @Override
    public void startDownloadFile(FileMetadata file) {
        downloadCount++;
    }
    
    @Override
    public void doneDownloads() {
        System.out.println("Downloaded " + downloadCount + " files");
    }
}
```

---

## Security

### Signing Configuration

```java
import org.update4j.*;
import java.security.*;
import java.nio.file.*;

KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

// Save keys for later use
Files.write(Paths.get("public.key"), keyPair.getPublic().getEncoded());
Files.write(Paths.get("private.key"), keyPair.getPrivate().getEncoded());

// Sign the configuration
Configuration signedConfig = Configuration.builder()
    .baseUri("https://example.com/")
    .file(FileMetadata.readFromPath(Paths.get("app.jar")))
    .sign(keyPair.getPrivate())
    .build();

// Write to file
try (Writer out = Files.newBufferedWriter(Paths.get("config.xml"))) {
    signedConfig.write(out);
}
```

### Verifying Configuration

```java
import org.update4j.*;
import java.security.*;
import java.nio.file.*;

// Load configuration with verification
PublicKey publicKey = KeyFactory.getInstance("RSA")
    .generatePublic(X509EncodedKeySpec(
        Files.readAllBytes(Paths.get("public.key"))));

Configuration config = Configuration.read(
    Files.newBufferedReader(Paths.get("config.xml")),
    publicKey  // Automatically verifies signature
);

// Or verify manually
config.verifyConfiguration(publicKey);
```

### Using Certificate File

```bash
# Command line with certificate
java -jar bootstrap.jar --remote https://example.com/config.xml --cert certificate.cer
```

The bootstrap will automatically:
1. Load the X.509 certificate
2. Validate certificate expiry
3. Use public key for signature verification
4. Warn if configuration is unsigned

### Security Best Practices

1. **Always sign your configuration** - Use RSA or EC keys
2. **Protect your private key** - Never include it in your application
3. **Use certificates** - Provides expiry validation and key management
4. **Check for warnings** - Unsigned configurations are a security risk
5. **Verify at runtime** - Always provide a public key when loading config

---

## Archive-Based Updates

### Creating an Archive

```java
// Create archive from directory
Archive archive = Archive.builder()
    .baseUri("https://example.com/updates/")
    .basePath(Paths.get("lib"))
    .output(Paths.get("update.zip"))
    .files(Files.list(Paths.get("lib"))
        .map(Path::toString)
        .map(FileMetadata::readFromPath)
        .collect(Collectors.toList()))
    .build();

// Or from existing configuration
Configuration config = Configuration.read(...);
Archive archive = Archive.builder()
    .configuration(config)
    .output(Paths.get("update.zip"))
    .build();
```

### Installing an Archive

```java
// Download and install
Path zip = Paths.get("update.zip");
config.update(UpdateOptions.archive(zip));
Archive.read(zip).install();
```

---

## Error Handling

### Handling Update Failures

```java
UpdateResult result = config.update();

if (result.getException() != null) {
    Throwable error = result.getException();
    System.err.println("Update failed: " + error.getMessage());
    error.printStackTrace();
} else {
    System.out.println("Update succeeded!");
}
```

### Custom Error Handling in UpdateHandler

```java
@Override
public void failed(Throwable t) {
    if (t instanceof IOException) {
        System.err.println("Network error: " + t.getMessage());
    } else if (t instanceof SecurityException) {
        System.err.println("Security error: " + t.getMessage());
    } else {
        System.err.println("Update failed: " + t.getMessage());
    }
    
    // Clean up partial downloads
    cleanupTempFiles();
}
```

---

## Advanced Usage

### Multiple Classloaders

```java
// Create isolated classloader for each module
ModuleLayer layer = ModuleLayer.defineModulesWithLoader(
    Configuration.empty().build(),
    List.of(modulePath),
    getClass().getClassLoader()
);

// Launch with specific layer
config.launch(layer, args);
```

### Custom Download Stream

```java
@Override
public InputStream openDownloadStream(FileMetadata file) throws Throwable {
    // Use custom authentication
    URL url = file.getUri().toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestProperty("Authorization", "Bearer " + getToken());
    return conn.getInputStream();
}
```

### Conditional File Updates

```java
@Override
public boolean shouldCheckForUpdate(FileMetadata file) {
    // Skip update check for optional files
    return !file.getPath().toString().contains("optional");
}
```

---

## Updating the Running Application

One of the most challenging tasks in any auto-update system is updating the JAR file that is currently running. Since Java locks JAR files while they're executing, you cannot directly overwrite them.

### The Delegate Pattern

Update4j solves this using a "delegate" pattern where one bootstrap downloads and launches another bootstrap:

```
bootstrap-v1.jar (running) → downloads bootstrap-v2.jar → launches bootstrap-v2.jar (new process)
                                                                    ↓
                                                            bootstrap-v1.jar can now be updated
```

### Setting Up Delegate Bootstrap

#### 1. Create Your Business Bootstrap

```java
public class MyBootstrap extends DefaultBootstrap {
    public static void main(String[] args) {
        Bootstrap.boot(args);
    }
}
```

#### 2. Configure Delegate in Your Config

```xml
<configuration>
    <property name="default.launcher.main.class" value="com.example.MyApp"/>
    
    <!-- The bootstrap that will be launched after update -->
    <property name="default.bootstrap.delegate" value="com.example.MyBootstrap"/>
    
    <files>
        <!-- Your application files -->
        <file path="myapp.jar" uri="https://example.com/updates/myapp.jar"/>
        
        <!-- The bootstrap itself -->
        <file path="bootstrap.jar" uri="https://example.com/updates/bootstrap-new.jar"/>
    </files>
</configuration>
```

#### 3. Run the Update Process

```java
// In your running bootstrap
Configuration config = Configuration.read(Files.newBufferedReader(Paths.get("config.xml")));

// This will:
// 1. Download all files including new bootstrap
// 2. Launch the delegate bootstrap (new process)
// 3. The new process can update the old bootstrap
config.update();
config.launch(); // This launches the delegate, not your current bootstrap
```

### How It Works

1. **Initial Run**: Your current bootstrap runs and checks for updates
2. **Download**: Downloads new bootstrap JAR to a temporary location
3. **Launch Delegate**: Launches the new bootstrap in a separate process
4. **Update Old**: The new bootstrap can now safely update the old JAR files (they're no longer locked)
5. **Cleanup**: Old bootstrap is removed or renamed

### Example: Complete Setup

```java
// MyBootstrap.java
public class MyBootstrap extends DefaultBootstrap {
    public static void main(String[] args) {
        Bootstrap.boot(args);
    }
}

// config.xml
/*
<configuration>
    <property name="app.name" value="MyApp"/>
    <property name="default.launcher.main.class" value="com.example.MyApp"/>
    <property name="default.bootstrap.delegate" value="com.example.MyBootstrap"/>
    
    <files>
        <file path="lib/myapp.jar" uri="myapp.jar"/>
        <file path="bootstrap.jar" uri="bootstrap.jar"/>
    </files>
</configuration>
*/
```

### Tips for Success

1. **Use Different Names**: Always download the new bootstrap with a different name, then rename after restart
2. **Two-Step Update**: Use `updateTemp()` for critical applications, finalize on next startup
3. **Keep Backups**: Keep old JAR until new one is verified working
4. **Test Thoroughly**: Test the update flow multiple times before production

### Common Issues

**Problem**: "File is locked" error when trying to update bootstrap

**Solution**: This is expected! Use the delegate pattern so the old bootstrap isn't running when you try to update it.

**Problem**: Delegate bootstrap doesn't start

**Solution**: Check that the delegate JAR is properly configured and all required files are present.

---

## Troubleshooting

### Enable Debug Mode

To troubleshoot issues, enable debug mode with the `--debug` flag:

```bash
java -jar bootstrap.jar --local config.xml --debug
```

Debug output includes:
- Configuration loading details
- File check and download progress
- Archive installation steps
- Launch information

Example debug output:
```
[DEBUG] Debug mode enabled
[DEBUG] Loading local configuration from: config.xml
[DEBUG] Loaded local config with 3 files
[DEBUG] Checking for updates...
[DEBUG] Launching application
```

### Custom Debug Output

You can also add your own debug output in custom UpdateHandler:

```java
if (DefaultBootstrap.isDebugEnabled()) {
    System.out.println("[DEBUG] My custom debug info: " + variable);
}
```

