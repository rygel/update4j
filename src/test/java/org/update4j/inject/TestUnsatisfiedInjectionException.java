package org.update4j.inject;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class TestUnsatisfiedInjectionException {

    @Test
    public void testConstructorWithField() throws Exception {
        class TestClass {
            private String field;
        }

        Field field = TestClass.class.getDeclaredField("field");

        UnsatisfiedInjectionException ex = new UnsatisfiedInjectionException(field);

        assertEquals("TestClass#field", ex.getMessage());
        assertEquals(field, ex.getTargetField());
    }

    @Test
    public void testGetTargetField() throws Exception {
        class TestClass {
            private String field;
        }

        Field field = TestClass.class.getDeclaredField("field");

        UnsatisfiedInjectionException ex = new UnsatisfiedInjectionException(field);

        assertSame(field, ex.getTargetField());
    }

    @Test
    public void testMessageFormat() throws Exception {
        class TestClass {
            private String testField;
        }

        Field field = TestClass.class.getDeclaredField("testField");

        UnsatisfiedInjectionException ex = new UnsatisfiedInjectionException(field);

        assertTrue(ex.getMessage().contains("TestClass"));
        assertTrue(ex.getMessage().contains("testField"));
        assertTrue(ex.getMessage().contains("#"));
    }

    @Test
    public void testExceptionExtendsException() {
        class TestClass {
            private String field;
        }

        UnsatisfiedInjectionException ex = assertThrows(UnsatisfiedInjectionException.class, () -> {
            throw new UnsatisfiedInjectionException(TestClass.class.getDeclaredField("field"));
        });

        assertNotNull(ex.getMessage());
    }
}
