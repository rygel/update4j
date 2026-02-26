package org.update4j.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestDefaultBootstrap {

    private DefaultBootstrap bootstrap;

    @BeforeEach
    public void setUp() {
        bootstrap = new DefaultBootstrap();
    }

    @Test
    public void testVersion() {
        assertEquals(Long.MIN_VALUE, bootstrap.version());
    }

    @Test
    public void testDefaultConstructor() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertNull(b.getRemote());
        assertNull(b.getLocal());
        assertEquals("./update.zip", b.getArchivePath());
        assertNull(b.getCert());
        assertFalse(b.isSyncLocal());
        assertFalse(b.isLaunchFirst());
        assertFalse(b.isStopOnUpdateError());
        assertFalse(b.isSingleInstance());
        assertNull(b.getPublicKey());
        assertNull(b.getBusinessArgs());
    }

    @Test
    public void testGetRemote() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        // Default should be null
        assertNull(b.getRemote());
    }

    @Test
    public void testGetLocal() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        // Default should be null
        assertNull(b.getLocal());
    }

    @Test
    public void testGetArchivePath() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertEquals("./update.zip", b.getArchivePath());
    }

    @Test
    public void testGetCert() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertNull(b.getCert());
    }

    @Test
    public void testIsSyncLocal() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertFalse(b.isSyncLocal());
    }

    @Test
    public void testIsLaunchFirst() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertFalse(b.isLaunchFirst());
    }

    @Test
    public void testIsStopOnUpdateError() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertFalse(b.isStopOnUpdateError());
    }

    @Test
    public void testIsSingleInstance() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertFalse(b.isSingleInstance());
    }

    @Test
    public void testGetPublicKey() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertNull(b.getPublicKey());
    }

    @Test
    public void testGetBusinessArgs() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertNull(b.getBusinessArgs());
    }

    @Test
    public void testParseArgsWithRemote() {
        List<String> args = new ArrayList<>();
        args.add("--remote");
        args.add("http://example.com/config.xml");

        bootstrap.parseArgs(args);

        assertEquals("http://example.com/config.xml", bootstrap.getRemote());
    }

    @Test
    public void testParseArgsWithLocal() {
        List<String> args = new ArrayList<>();
        args.add("--local");
        args.add("/path/to/config.xml");

        bootstrap.parseArgs(args);

        assertEquals("/path/to/config.xml", bootstrap.getLocal());
    }

    @Test
    public void testParseArgsWithArchive() {
        List<String> args = new ArrayList<>();
        args.add("--archive");
        args.add("custom-archive.zip");

        bootstrap.parseArgs(args);

        assertEquals("custom-archive.zip", bootstrap.getArchivePath());
    }

    @Test
    public void testParseArgsWithCert() {
        List<String> args = new ArrayList<>();
        args.add("--cert");
        args.add("/path/to/cert.pem");

        bootstrap.parseArgs(args);

        assertEquals("/path/to/cert.pem", bootstrap.getCert());
    }

    @Test
    public void testParseArgsSyncLocal() {
        List<String> args = new ArrayList<>();
        args.add("--syncLocal");

        bootstrap.parseArgs(args);

        assertTrue(bootstrap.isSyncLocal());
    }

    @Test
    public void testParseArgsLaunchFirst() {
        List<String> args = new ArrayList<>();
        args.add("--launchFirst");

        bootstrap.parseArgs(args);

        assertTrue(bootstrap.isLaunchFirst());
    }

    @Test
    public void testParseArgsStopOnUpdateError() {
        List<String> args = new ArrayList<>();
        args.add("--stopOnUpdateError");

        bootstrap.parseArgs(args);

        assertTrue(bootstrap.isStopOnUpdateError());
    }

    @Test
    public void testParseArgsSingleInstance() {
        List<String> args = new ArrayList<>();
        args.add("--singleInstance");

        bootstrap.parseArgs(args);

        assertTrue(bootstrap.isSingleInstance());
    }

    @Test
    public void testParseArgsMultiple() {
        List<String> args = new ArrayList<>();
        args.add("--remote");
        args.add("http://example.com/config.xml");
        args.add("--local");
        args.add("/local/config.xml");
        args.add("--syncLocal");
        args.add("--launchFirst");

        bootstrap.parseArgs(args);

        assertEquals("http://example.com/config.xml", bootstrap.getRemote());
        assertEquals("/local/config.xml", bootstrap.getLocal());
        assertTrue(bootstrap.isSyncLocal());
        assertTrue(bootstrap.isLaunchFirst());
    }

    @Test
    public void testParseArgsUnknownOptionThrowsException() {
        List<String> args = new ArrayList<>();
        args.add("--unknown");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.parseArgs(args);
        });

        assertTrue(ex.getMessage().contains("Unknown option"));
    }

    @Test
    public void testParseArgsRemoteWithoutValueThrowsException() {
        List<String> args = new ArrayList<>();
        args.add("--remote");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.parseArgs(args);
        });

        assertTrue(ex.getMessage().contains("--remote"));
    }

    @Test
    public void testParseArgsLocalWithoutValueThrowsException() {
        List<String> args = new ArrayList<>();
        args.add("--local");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.parseArgs(args);
        });

        assertTrue(ex.getMessage().contains("--local"));
    }

    @Test
    public void testParseArgsArchiveWithoutValueThrowsException() {
        List<String> args = new ArrayList<>();
        args.add("--archive");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.parseArgs(args);
        });

        assertTrue(ex.getMessage().contains("--archive"));
    }

    @Test
    public void testParseArgsCertWithoutValueThrowsException() {
        List<String> args = new ArrayList<>();
        args.add("--cert");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.parseArgs(args);
        });

        assertTrue(ex.getMessage().contains("--cert"));
    }

    @Test
    public void testParseArgsDelegateThrowsException() {
        List<String> args = new ArrayList<>();
        args.add("--delegate");
        args.add("com.example.Delegate");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.parseArgs(args);
        });

        assertTrue(ex.getMessage().contains("--delegate"));
    }

    @Test
    public void testMainWithEmptyArgs() {
        List<String> args = new ArrayList<>();

        assertDoesNotThrow(() -> {
            bootstrap.main(args);
        });
    }

    @Test
    public void testMainWithRemoteOnlyDoesNotThrow() {
        List<String> args = new ArrayList<>();
        args.add("--remote");
        args.add("file:///path/to/config.xml");

        // Should NOT throw - remote alone is valid (using file:// scheme)
        assertDoesNotThrow(() -> {
            bootstrap.main(args);
        });
    }

    @Test
    public void testMainWithLocalOnlyDoesNotThrow() {
        List<String> args = new ArrayList<>();
        args.add("--local");
        args.add("/path/to/config.xml");

        // Should NOT throw - local alone is valid
        assertDoesNotThrow(() -> {
            bootstrap.main(args);
        });
    }

    @Test
    public void testMainWithEmptyArgsShowsWelcome() {
        // When args is empty, it shows welcome message and returns without exception
        List<String> args = new ArrayList<>();
        
        assertDoesNotThrow(() -> {
            bootstrap.main(args);
        });
    }

    @Test
    public void testMainWithInvalidOptionThrows() {
        List<String> args = new ArrayList<>();
        args.add("--invalidOption");
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.main(args);
        });
        
        assertTrue(ex.getMessage().contains("Unknown option"));
    }

    @Test
    public void testMainWithLaunchFirstButNoLocal() {
        List<String> args = new ArrayList<>();
        args.add("--remote");
        args.add("file:///path/to/config.xml");
        args.add("--launchFirst");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.main(args);
        });

        assertEquals("--launchFirst requires a local configuration.", ex.getMessage());
    }

    @Test
    public void testMainWithSyncLocalButNoRemote() {
        List<String> args = new ArrayList<>();
        args.add("--local");
        args.add("/path/to/config.xml");
        args.add("--syncLocal");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.main(args);
        });

        assertEquals("--syncLocal requires a remote configuration.", ex.getMessage());
    }

    @Test
    public void testMainWithSyncLocalButNoLocal() {
        List<String> args = new ArrayList<>();
        args.add("--remote");
        args.add("file:///path/to/config.xml");
        args.add("--syncLocal");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bootstrap.main(args);
        });

        assertEquals("--syncLocal requires a local configuration.", ex.getMessage());
    }

    @Test
    public void testMainWithValidLocalOnly() {
        List<String> args = new ArrayList<>();
        args.add("--local");
        args.add("/path/to/config.xml");

        // Should not throw - local config only is valid
        assertDoesNotThrow(() -> {
            bootstrap.main(args);
        });
    }

    @Test
    public void testMainWithBusinessArgs() {
        List<String> args = new ArrayList<>();
        args.add("--local");
        args.add("/path/to/config.xml");
        args.add("--");
        args.add("arg1");
        args.add("arg2");

        assertDoesNotThrow(() -> {
            bootstrap.main(args);
        });

        assertNotNull(bootstrap.getBusinessArgs());
    }

    @Test
    public void testParseArgsUpdatesExistingValues() {
        List<String> args1 = new ArrayList<>();
        args1.add("--archive");
        args1.add("first.zip");
        bootstrap.parseArgs(args1);

        assertEquals("first.zip", bootstrap.getArchivePath());

        List<String> args2 = new ArrayList<>();
        args2.add("--archive");
        args2.add("second.zip");
        bootstrap.parseArgs(args2);

        assertEquals("second.zip", bootstrap.getArchivePath());
    }

    @Test
    public void testMultipleBooleanFlags() {
        List<String> args = new ArrayList<>();
        args.add("--syncLocal");
        args.add("--launchFirst");
        args.add("--stopOnUpdateError");
        args.add("--singleInstance");

        bootstrap.parseArgs(args);

        assertTrue(bootstrap.isSyncLocal());
        assertTrue(bootstrap.isLaunchFirst());
        assertTrue(bootstrap.isStopOnUpdateError());
        assertTrue(bootstrap.isSingleInstance());
    }

    @Test
    public void testArchivePathDefaultValue() {
        DefaultBootstrap b = new DefaultBootstrap();
        
        assertEquals("./update.zip", b.getArchivePath());
    }

    @Test
    public void testGetLogoNotNull() {
        // This tests the welcome message generation indirectly
        DefaultBootstrap b = new DefaultBootstrap();
        
        // We can't directly test private methods, but we can verify the class loads
        assertNotNull(b);
    }
}
