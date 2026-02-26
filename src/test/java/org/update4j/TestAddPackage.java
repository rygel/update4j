package org.update4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAddPackage {

    @Test
    public void testConstructorWithValidArguments() {
        AddPackage addPackage = new AddPackage("com.example.package", "com.example.module");

        assertEquals("com.example.package", addPackage.getPackageName());
        assertEquals("com.example.module", addPackage.getTargetModule());
    }

    @Test
    public void testConstructorWithNullPackageName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new AddPackage(null, "com.example.module");
        });

        assertEquals("Missing package name.", ex.getMessage());
    }

    @Test
    public void testConstructorWithEmptyPackageName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new AddPackage("", "com.example.module");
        });

        assertEquals("Missing package name.", ex.getMessage());
    }

    @Test
    public void testConstructorWithNullTargetModule() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new AddPackage("com.example.package", null);
        });

        assertEquals("Missing target module name.", ex.getMessage());
    }

    @Test
    public void testConstructorWithEmptyTargetModule() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new AddPackage("com.example.package", "");
        });

        assertEquals("Missing target module name.", ex.getMessage());
    }

    @Test
    public void testGetPackageName() {
        AddPackage addPackage = new AddPackage("my.package", "target.module");

        assertEquals("my.package", addPackage.getPackageName());
    }

    @Test
    public void testGetTargetModule() {
        AddPackage addPackage = new AddPackage("my.package", "target.module");

        assertEquals("target.module", addPackage.getTargetModule());
    }

    @Test
    public void testWhitespaceInPackageName() {
        AddPackage addPackage = new AddPackage("  com.example.package  ", "com.example.module");

        assertEquals("  com.example.package  ", addPackage.getPackageName());
    }

    @Test
    public void testWhitespaceInTargetModule() {
        AddPackage addPackage = new AddPackage("com.example.package", "  com.example.module  ");

        assertEquals("  com.example.module  ", addPackage.getTargetModule());
    }

    @Test
    public void testTypicalModuleSystemUsage() {
        AddPackage addExports = new AddPackage("com.mycompany.internal", "javafx.graphics");
        AddPackage addOpens = new AddPackage("com.mycompany.reflection", "spring.core");

        assertEquals("com.mycompany.internal", addExports.getPackageName());
        assertEquals("javafx.graphics", addExports.getTargetModule());
        
        assertEquals("com.mycompany.reflection", addOpens.getPackageName());
        assertEquals("spring.core", addOpens.getTargetModule());
    }
}
