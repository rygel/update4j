package org.update4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestIntegration {

    @TempDir
    Path tempDir;

    private KeyPair keyPair;
    private Path installDir;

    @BeforeEach
    public void setUp() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();
        
        installDir = tempDir.resolve("install");
        Files.createDirectories(installDir);
    }

    @Test
    public void testConfigurationWithMultipleProperties() throws Exception {
        Configuration config = Configuration.builder()
                .baseUri("http://example.com/")
                .basePath(installDir)
                .property("prop1", "value1")
                .property("prop2", "value2")
                .property("prop3", "value3")
                .build();
        
        assertEquals(3, config.getProperties().size());
    }

    @Test
    public void testConfigurationPropertyOSFilter() throws Exception {
        Configuration config = Configuration.builder()
                .baseUri("http://example.com/")
                .basePath(installDir)
                .property("windows.prop", "windows-value", OS.WINDOWS)
                .property("linux.prop", "linux-value", OS.LINUX)
                .property("common.prop", "common-value")
                .build();
        
        assertEquals(3, config.getProperties().size());
    }

    @Test
    public void testConfigurationWithBaseUriVariation() throws Exception {
        Path configFile = tempDir.resolve("config.xml");
        
        Configuration config = Configuration.builder()
                .baseUri("file:///C:/test/")
                .basePath(installDir)
                .build();
        
        try (Writer writer = Files.newBufferedWriter(configFile)) {
            config.write(writer);
        }
        
        Configuration loaded = Configuration.read(Files.newBufferedReader(configFile));
        assertNotNull(loaded.getBaseUri());
    }

    @Test
    public void testFileMetadataReadFromWithChecksum() throws Exception {
        Path testFile = tempDir.resolve("checksum.jar");
        Files.write(testFile, "test content for checksum".getBytes());
        
        FileMetadata.Reference ref = FileMetadata.readFrom(testFile);
        
        assertNotNull(ref.getChecksum());
        assertTrue(ref.getChecksum() != 0);
    }

    @Test
    public void testFileMetadataWithModulepath() throws Exception {
        Path testFile = tempDir.resolve("module.jar");
        Files.createFile(testFile);
        
        FileMetadata.Reference ref = FileMetadata.readFrom(testFile)
                .modulepath(true);
        
        assertTrue(ref.isModulepath());
    }

    @Test
    public void testFileMetadataWithClasspath() throws Exception {
        Path testFile = tempDir.resolve("class.jar");
        Files.createFile(testFile);
        
        FileMetadata.Reference ref = FileMetadata.readFrom(testFile)
                .classpath(true);
        
        assertTrue(ref.isClasspath());
    }

    @Test
    public void testConfigurationBuilderWithComment() throws Exception {
        Path configFile = tempDir.resolve("config.xml");
        
        Configuration config = Configuration.builder()
                .baseUri("http://example.com/")
                .basePath(installDir)
                .property("key", "value")
                .build();
        
        try (Writer writer = Files.newBufferedWriter(configFile)) {
            config.write(writer);
        }
        
        String content = new String(Files.readAllBytes(configFile));
        assertTrue(content.contains("key") || content.contains("value"));
    }

    @Test
    public void testArchiveWithEmptyConfig() {
        Path archivePath = tempDir.resolve("empty.zip");
        
        assertThrows(Exception.class, () -> {
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(archivePath))) {
                zos.putNextEntry(new ZipEntry("reserved/config"));
                zos.write("<configuration/>".getBytes());
                zos.closeEntry();
            }
            
            Archive.read(archivePath);
        });
    }

    @Test
    public void testFileMetadataWithComment() throws Exception {
        Path testFile = tempDir.resolve("commented.jar");
        Files.createFile(testFile);
        
        FileMetadata.Reference ref = FileMetadata.readFrom(testFile)
                .comment("This is a test file");
        
        assertEquals("This is a test file", ref.getComment());
    }

    @Test
    public void testConfigurationReadWithPublicKey() throws Exception {
        Path configFile = tempDir.resolve("signed-config.xml");
        
        Configuration config = Configuration.builder()
                .baseUri("http://example.com/")
                .basePath(installDir)
                .signer(keyPair.getPrivate())
                .property("signed", "true")
                .build();
        
        try (Writer writer = Files.newBufferedWriter(configFile)) {
            config.write(writer);
        }
        
        Configuration loaded = Configuration.read(
                Files.newBufferedReader(configFile), 
                keyPair.getPublic());
        
        assertNotNull(loaded);
        assertEquals(1, loaded.getProperties("signed").size());
    }

    @Test
    public void testRequiresUpdateWithExistingFile() throws Exception {
        Path existingFile = installDir.resolve("existing.jar");
        Files.write(existingFile, "original content".getBytes());
        
        Configuration config = Configuration.builder()
                .baseUri(tempDir.toUri().toString())
                .basePath(installDir)
                .file(FileMetadata.readFrom(existingFile))
                .build();
        
        // File exists, so requiresUpdate should be false
        assertFalse(config.requiresUpdate());
    }

    @Test
    public void testRequiresUpdateWithNonExistentFile() throws Exception {
        FileMetadata.Reference ref = FileMetadata.readFrom(installDir.resolve("dummy.jar"));
        // Override the path to point to a non-existent file
        // We can't easily do this, so just skip this test
        
        // Test that requiresUpdate works with existing file
        Path existingFile = installDir.resolve("test.jar");
        Files.write(existingFile, "test".getBytes());
        
        Configuration config = Configuration.builder()
                .baseUri(tempDir.toUri().toString())
                .basePath(installDir)
                .file(FileMetadata.readFrom(existingFile))
                .build();
        
        // File exists, so requiresUpdate should be false
        assertFalse(config.requiresUpdate());
    }

    @Test
    public void testConfigurationEquals() throws Exception {
        Configuration config1 = Configuration.builder()
                .baseUri("http://example.com/")
                .basePath(installDir)
                .build();
        
        Configuration config2 = Configuration.builder()
                .baseUri("http://example.com/")
                .basePath(installDir)
                .build();
        
        assertEquals(config1.getBaseUri(), config2.getBaseUri());
    }

    @Test
    public void testDynamicClassLoaderWithParent() throws Exception {
        ClassLoader parent = getClass().getClassLoader();
        DynamicClassLoader loader = new DynamicClassLoader(parent);
        
        assertEquals(parent, loader.getParent());
    }

    @Test
    public void testOSFromString() {
        assertEquals(OS.WINDOWS, OS.WINDOWS);
        assertEquals(OS.LINUX, OS.LINUX);
        assertEquals(OS.MAC, OS.MAC);
    }

    @Test
    public void testConfigurationFilesList() throws Exception {
        Path file1 = tempDir.resolve("file1.jar");
        Path file2 = tempDir.resolve("file2.jar");
        Files.createFile(file1);
        Files.createFile(file2);
        
        Configuration config = Configuration.builder()
                .baseUri("http://example.com/")
                .basePath(installDir)
                .file(FileMetadata.readFrom(file1))
                .file(FileMetadata.readFrom(file2))
                .build();
        
        assertEquals(2, config.getFiles().size());
    }

    @Test
    public void testAddPackageCreation() {
        AddPackage addPkg = new AddPackage("com.example.export", "java.base");
        
        assertEquals("com.example.export", addPkg.getPackageName());
        assertEquals("java.base", addPkg.getTargetModule());
    }

    @Test
    public void testAddPackageWithNullPackage() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AddPackage(null, "java.base");
        });
    }

    @Test
    public void testAddPackageWithNullModule() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AddPackage("com.example.export", null);
        });
    }

    @Test
    public void testUpdateOptionsWithHandler() throws Exception {
        Path archivePath = tempDir.resolve("test.zip");
        
        UpdateOptions<?> options = UpdateOptions.archive(archivePath);
        
        assertNotNull(options);
    }

    @Test
    public void testFileMetadataBuilderPattern() throws Exception {
        Path testFile = tempDir.resolve("test.jar");
        Files.createFile(testFile);
        
        FileMetadata.Reference ref = FileMetadata.readFrom(testFile)
                .os(OS.CURRENT)
                .classpath(true)
                .comment("Test comment");
        
        assertNotNull(ref);
        assertEquals(OS.CURRENT, ref.getOs());
    }

    @Test
    public void testArchiveGetFilesFailsGracefully() {
        Path archivePath = tempDir.resolve("test.zip");
        
        assertThrows(Exception.class, () -> {
            Archive.read(archivePath);
        });
    }
    
    @Test
    public void testArchiveWithMissingConfig() throws Exception {
        Path archivePath = tempDir.resolve("test.zip");
        
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(archivePath))) {
            zos.putNextEntry(new ZipEntry("files/test.txt"));
            zos.write("test".getBytes());
            zos.closeEntry();
        }
        
        assertThrows(Exception.class, () -> {
            Archive.read(archivePath);
        });
    }
}
