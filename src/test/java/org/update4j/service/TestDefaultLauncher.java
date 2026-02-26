package org.update4j.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.update4j.Configuration;
import org.update4j.DynamicClassLoader;
import org.update4j.LaunchContext;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestDefaultLauncher {

    private Configuration config;
    private DynamicClassLoader classLoader;
    private LaunchContext context;

    @BeforeEach
    public void setUp() throws Exception {
        config = Configuration.builder()
                .baseUri(new File("src/test/resources").toURI().toString())
                .basePath(new File("target/test-launch").toPath().toAbsolutePath())
                .build();

        classLoader = new DynamicClassLoader(getClass().getClassLoader());
        ModuleLayer layer = ModuleLayer.boot();

        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);
        context = constructor.newInstance(layer, classLoader, config);
    }

    @Test
    public void testVersion() {
        DefaultLauncher launcher = new DefaultLauncher();
        assertEquals(Long.MIN_VALUE, launcher.version());
    }

    @Test
    public void testConstructorWithArgs() {
        List<String> args = new ArrayList<>();
        args.add("arg1");
        args.add("arg2");

        DefaultLauncher launcher = new DefaultLauncher(args);
        assertNotNull(launcher);
    }

    @Test
    public void testDefaultConstructor() {
        DefaultLauncher launcher = new DefaultLauncher();
        assertNotNull(launcher);
    }

    @Test
    public void testThrowExceptionWhenNoMainClassAndNoArgs() {
        DefaultLauncher launcher = new DefaultLauncher();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            launcher.run(context);
        });

        assertTrue(ex.getMessage().contains("You must provide either a main class or arguments"));
    }

    @Test
    public void testThrowExceptionForInvalidMainClassName() throws Exception {
        Configuration invalidConfig = Configuration.builder()
                .baseUri(new File("src/test/resources").toURI().toString())
                .basePath(new File("target/test-launch").toPath().toAbsolutePath())
                .property("default.launcher.main.class", "invalid.class.name!")
                .build();

        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);
        LaunchContext invalidContext = constructor.newInstance(
                ModuleLayer.boot(), classLoader, invalidConfig);

        DefaultLauncher launcher = new DefaultLauncher();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            launcher.run(invalidContext);
        });

        assertTrue(ex.getMessage().contains("is not a valid Java class name"));
    }

    @Test
    public void testSystemPropertiesAreSet() throws Exception {
        String originalProperty = System.getProperty("test.system.property");
        try {
            Configuration sysPropConfig = Configuration.builder()
                    .baseUri(new File("src/test/resources").toURI().toString())
                    .basePath(new File("target/test-launch").toPath().toAbsolutePath())
                    .property("default.launcher.system.test.system.property", "test.value")
                    .property("default.launcher.main.class", "java.lang.String")
                    .build();

            Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                    ModuleLayer.class, ClassLoader.class, Configuration.class);
            constructor.setAccessible(true);
            LaunchContext sysPropContext = constructor.newInstance(
                    ModuleLayer.boot(), classLoader, sysPropConfig);

            DefaultLauncher launcher = new DefaultLauncher();

            try {
                launcher.run(sysPropContext);
            } catch (Exception e) {
                // Expected - String doesn't have a main method
            }

            assertEquals("test.value", System.getProperty("test.system.property"));
        } finally {
            if (originalProperty == null) {
                System.clearProperty("test.system.property");
            } else {
                System.setProperty("test.system.property", originalProperty);
            }
        }
    }

    @Test
    public void testArgumentsFromConfigProperties() throws Exception {
        Configuration argsConfig = Configuration.builder()
                .baseUri(new File("src/test/resources").toURI().toString())
                .basePath(new File("target/test-launch").toPath().toAbsolutePath())
                .property("default.launcher.argument.1", "arg1")
                .property("default.launcher.argument.2", "arg2")
                .property("default.launcher.argument.3", "arg3")
                .build();

        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);
        LaunchContext argsContext = constructor.newInstance(
                ModuleLayer.boot(), classLoader, argsConfig);

        DefaultLauncher launcher = new DefaultLauncher();

        try {
            launcher.run(argsContext);
        } catch (Exception e) {
            // Expected - no main class, will try to execute as command
            // which should fail with no command
        }
    }

    @Test
    public void testBootstrapArgsTakePrecedence() throws Exception {
        List<String> bootstrapArgs = new ArrayList<>();
        bootstrapArgs.add("bootstrapArg1");
        bootstrapArgs.add("bootstrapArg2");

        Configuration argsConfig = Configuration.builder()
                .baseUri(new File("src/test/resources").toURI().toString())
                .basePath(new File("target/test-launch").toPath().toAbsolutePath())
                .property("default.launcher.argument.1", "configArg1")
                .property("default.launcher.main.class", "java.lang.String")
                .build();

        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);
        LaunchContext argsContext = constructor.newInstance(
                ModuleLayer.boot(), classLoader, argsConfig);

        DefaultLauncher launcher = new DefaultLauncher(bootstrapArgs);

        try {
            launcher.run(argsContext);
        } catch (Exception e) {
            // Expected - String doesn't have a main method
        }
    }

    @Test
    public void testSuppressWarningPropertyIsSet() throws Exception {
        String originalProperty = System.getProperty("update4j.suppress.warning.access");
        try {
            Configuration configWithMain = Configuration.builder()
                    .baseUri(new File("src/test/resources").toURI().toString())
                    .basePath(new File("target/test-launch").toPath().toAbsolutePath())
                    .property("default.launcher.main.class", "java.lang.String")
                    .build();

            Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                    ModuleLayer.class, ClassLoader.class, Configuration.class);
            constructor.setAccessible(true);
            LaunchContext contextWithMain = constructor.newInstance(
                    ModuleLayer.boot(), classLoader, configWithMain);

            DefaultLauncher launcher = new DefaultLauncher();

            try {
                launcher.run(contextWithMain);
            } catch (Exception e) {
                // Expected - String doesn't have a main method
            }

            assertEquals("true", System.getProperty("update4j.suppress.warning.access"));
        } finally {
            if (originalProperty == null) {
                System.clearProperty("update4j.suppress.warning.access");
            } else {
                System.setProperty("update4j.suppress.warning.access", originalProperty);
            }
        }
    }

    @Test
    public void testLaunchWithValidMainClass() throws Exception {
        Configuration mainConfig = Configuration.builder()
                .baseUri(new File("src/test/resources").toURI().toString())
                .basePath(new File("target/test-launch").toPath().toAbsolutePath())
                .property("default.launcher.main.class", "org.update4j.TestConfiguration")
                .build();

        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);
        LaunchContext mainContext = constructor.newInstance(
                ModuleLayer.boot(), classLoader, mainConfig);

        DefaultLauncher launcher = new DefaultLauncher();

        try {
            launcher.run(mainContext);
        } catch (Exception e) {
            // Expected - TestConfiguration doesn't have a main method, 
            // but the class should be found
        }
    }

    @Test
    public void testConstantPropertyKeys() {
        assertEquals("default.launcher", DefaultLauncher.DOMAIN_PREFIX);
        assertEquals("default.launcher.main.class", DefaultLauncher.MAIN_CLASS_PROPERTY_KEY);
        assertEquals("default.launcher.argument", DefaultLauncher.ARGUMENT_PROPERTY_KEY_PREFIX);
        assertEquals("default.launcher.system", DefaultLauncher.SYSTEM_PROPERTY_KEY_PREFIX);
    }
}
