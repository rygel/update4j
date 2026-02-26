package org.update4j;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

public class TestLaunchContext {

    @Test
    public void testConstructorWithNullModuleLayer() throws Exception {
        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> {
            constructor.newInstance(null, getClass().getClassLoader(), Configuration.builder().build());
        });

        assertTrue(ex.getCause() instanceof NullPointerException);
    }

    @Test
    public void testConstructorWithNullClassLoader() throws Exception {
        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> {
            constructor.newInstance(ModuleLayer.boot(), null, Configuration.builder().build());
        });

        assertTrue(ex.getCause() instanceof NullPointerException);
    }

    @Test
    public void testConstructorWithNullConfiguration() throws Exception {
        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> {
            constructor.newInstance(ModuleLayer.boot(), getClass().getClassLoader(), null);
        });

        assertTrue(ex.getCause() instanceof NullPointerException);
    }

    @Test
    public void testGetModuleLayer() throws Exception {
        Configuration config = Configuration.builder()
                .baseUri("http://example.com/")
                .build();

        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);

        LaunchContext context = constructor.newInstance(
                ModuleLayer.boot(), getClass().getClassLoader(), config);

        assertEquals(ModuleLayer.boot(), context.getModuleLayer());
    }

    @Test
    public void testGetClassLoader() throws Exception {
        Configuration config = Configuration.builder()
                .baseUri("http://example.com/")
                .build();

        ClassLoader expectedLoader = getClass().getClassLoader();

        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);

        LaunchContext context = constructor.newInstance(
                ModuleLayer.boot(), expectedLoader, config);

        assertEquals(expectedLoader, context.getClassLoader());
    }

    @Test
    public void testGetConfiguration() throws Exception {
        Configuration config = Configuration.builder()
                .baseUri("http://example.com/")
                .build();

        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);

        LaunchContext context = constructor.newInstance(
                ModuleLayer.boot(), getClass().getClassLoader(), config);

        assertEquals(config, context.getConfiguration());
    }

    @Test
    public void testConstructorWithValidArguments() throws Exception {
        Configuration config = Configuration.builder()
                .baseUri("http://example.com/")
                .build();

        ClassLoader loader = getClass().getClassLoader();

        Constructor<LaunchContext> constructor = LaunchContext.class.getDeclaredConstructor(
                ModuleLayer.class, ClassLoader.class, Configuration.class);
        constructor.setAccessible(true);

        assertDoesNotThrow(() -> {
            LaunchContext context = constructor.newInstance(ModuleLayer.boot(), loader, config);
            assertNotNull(context);
        });
    }
}
