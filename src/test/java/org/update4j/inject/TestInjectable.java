package org.update4j.inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class TestInjectable {

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testUnidirectionalInjection() throws Exception {
        TestSource source = new TestSource();
        source.setStringValue("Hello");
        source.setIntValue(42);

        TestTarget target = new TestTarget();

        Injectable.injectUnidirectional(source, target);

        assertEquals("Hello", target.getStringValue());
        assertEquals(42, target.getIntValue());
        assertEquals("Hello", source.getStringValue());
        assertEquals(42, source.getIntValue());
    }

    @Test
    public void testBidirectionalInjection() throws Exception {
        TestBidirectional obj1 = new TestBidirectional();
        TestBidirectional obj2 = new TestBidirectional();

        obj1.setValue1("From 1");
        obj1.setValue2("100");

        obj2.setValue1("From 2");
        obj2.setValue2("200");

        Injectable.injectBidirectional(obj1, obj2);

        assertEquals("From 2", obj1.getValue1());
        assertEquals("200", obj1.getValue2());
        assertEquals("From 1", obj2.getValue1());
        assertEquals("100", obj2.getValue2());
    }

    @Test
    public void testExplicitTargetName() throws Exception {
        class ExplicitSource implements Injectable {
            @InjectSource(target = "targetField")
            private String sourceField;

            public String getSourceField() {
                return sourceField;
            }

            public void setSourceField(String value) {
                this.sourceField = value;
            }
        }

        class ExplicitTarget implements Injectable {
            @InjectTarget
            private String targetField;

            public String getTargetField() {
                return targetField;
            }
        }

        ExplicitSource src = new ExplicitSource();
        src.setSourceField("test value");

        ExplicitTarget tgt = new ExplicitTarget();

        Injectable.injectUnidirectional(src, tgt);

        assertEquals("test value", tgt.getTargetField());
    }

    @Test
    public void testRequiredFieldWithSourceSucceeds() throws Exception {
        class RequiredSource implements Injectable {
            @InjectSource
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        class RequiredTarget implements Injectable {
            @InjectTarget(required = true)
            private String value;

            public String getValue() {
                return value;
            }
        }

        RequiredSource src = new RequiredSource();
        src.setValue("test");

        RequiredTarget tgt = new RequiredTarget();

        assertDoesNotThrow(() -> {
            Injectable.injectUnidirectional(src, tgt);
        });

        assertEquals("test", tgt.getValue());
    }

    @Test
    public void testRequiredFieldNoSourceThrowsException() {
        class RequiredSource implements Injectable {
            @InjectSource
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        class RequiredTarget implements Injectable {
            @InjectTarget(required = true)
            private String differentValue;

            public String getDifferentValue() {
                return differentValue;
            }
        }

        RequiredSource src = new RequiredSource();
        src.setValue("test");

        RequiredTarget tgt = new RequiredTarget();

        assertThrows(UnsatisfiedInjectionException.class, () -> {
            Injectable.injectUnidirectional(src, tgt);
        });
    }

    @Test
    public void testNonRequiredFieldWithSourceSucceeds() throws Exception {
        class OptionalSource implements Injectable {
            @InjectSource
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        class OptionalTarget implements Injectable {
            @InjectTarget(required = false)
            private String value;

            public String getValue() {
                return value;
            }
        }

        OptionalSource src = new OptionalSource();
        src.setValue("test");

        OptionalTarget tgt = new OptionalTarget();

        assertDoesNotThrow(() -> {
            Injectable.injectUnidirectional(src, tgt);
        });

        assertEquals("test", tgt.getValue());
    }

    @Test
    public void testNonRequiredFieldNoSourceDoesNotThrowException() throws Exception {
        class OptionalSource implements Injectable {
            @InjectSource
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        class OptionalTarget implements Injectable {
            @InjectTarget(required = false)
            private String differentValue;

            public String getDifferentValue() {
                return differentValue;
            }
        }

        OptionalSource src = new OptionalSource();
        src.setValue("test");

        OptionalTarget tgt = new OptionalTarget();

        assertDoesNotThrow(() -> {
            Injectable.injectUnidirectional(src, tgt);
        });

        assertNull(tgt.getDifferentValue());
    }

    @Test
    public void testPostInjectCallbackWithoutParameter() throws Exception {
        class CallbackTarget implements Injectable {
            @InjectTarget
            private String value;

            private boolean callbackCalled = false;

            @PostInject
            private void onInject() {
                callbackCalled = true;
            }

            public String getValue() {
                return value;
            }

            public boolean isCallbackCalled() {
                return callbackCalled;
            }
        }

        class SimpleSource implements Injectable {
            @InjectSource
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        SimpleSource src = new SimpleSource();
        src.setValue("test");

        CallbackTarget tgt = new CallbackTarget();

        Injectable.injectUnidirectional(src, tgt);

        assertTrue(tgt.isCallbackCalled());
    }

    @Test
    public void testPostInjectCallbackWithParameter() throws Exception {
        class CallbackSource implements Injectable {
            @InjectSource
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        class CallbackTarget implements Injectable {
            @InjectTarget
            private String value;

            private Injectable receivedSource = null;

            @PostInject
            private void onInject(Injectable source) {
                receivedSource = source;
            }

            public String getValue() {
                return value;
            }

            public Injectable getReceivedSource() {
                return receivedSource;
            }
        }

        CallbackSource src = new CallbackSource();
        src.setValue("test");

        CallbackTarget tgt = new CallbackTarget();

        Injectable.injectUnidirectional(src, tgt);

        assertSame(src, tgt.getReceivedSource());
    }

    @Test
    public void testMultipleSourcesInjectToSingleTarget() throws Exception {
        class MultiSource implements Injectable {
            @InjectSource(target = "targetField1")
            private String sourceField1;

            @InjectSource(target = "targetField2")
            private String sourceField2;

            public String getSourceField1() {
                return sourceField1;
            }

            public String getSourceField2() {
                return sourceField2;
            }

            public void setSourceField1(String value) {
                this.sourceField1 = value;
            }

            public void setSourceField2(String value) {
                this.sourceField2 = value;
            }
        }

        class MultiTarget implements Injectable {
            @InjectTarget
            private String targetField1;

            @InjectTarget
            private String targetField2;

            public String getTargetField1() {
                return targetField1;
            }

            public String getTargetField2() {
                return targetField2;
            }
        }

        MultiSource src = new MultiSource();
        src.setSourceField1("value1");
        src.setSourceField2("value2");

        MultiTarget tgt = new MultiTarget();

        Injectable.injectUnidirectional(src, tgt);

        assertEquals("value1", tgt.getTargetField1());
        assertEquals("value2", tgt.getTargetField2());
    }

    @Test
    public void testPrivateFieldsAreAccessible() throws Exception {
        class PrivateFieldSource implements Injectable {
            @InjectSource
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        class PrivateFieldTarget implements Injectable {
            @InjectTarget
            private String value;

            public String getValue() {
                return value;
            }
        }

        PrivateFieldSource src = new PrivateFieldSource();
        src.setValue("private test");

        PrivateFieldTarget tgt = new PrivateFieldTarget();

        assertDoesNotThrow(() -> {
            Injectable.injectUnidirectional(src, tgt);
        });

        assertEquals("private test", tgt.getValue());
    }

    @Test
    public void testTwoSourcesWithSameKeyThrowsException() {
        class DuplicateSource implements Injectable {
            @InjectSource(target = "targetField")
            private String value1;

            @InjectSource(target = "targetField")
            private String value2;

            public String getValue1() {
                return value1;
            }

            public String getValue2() {
                return value2;
            }

            public void setValue1(String value) {
                this.value1 = value;
            }

            public void setValue2(String value) {
                this.value2 = value;
            }
        }

        class SimpleTarget implements Injectable {
            @InjectTarget
            private String targetField;

            public String getTargetField() {
                return targetField;
            }
        }

        DuplicateSource src = new DuplicateSource();
        src.setValue1("test1");

        SimpleTarget tgt = new SimpleTarget();

        assertThrows(IllegalArgumentException.class, () -> {
            Injectable.injectUnidirectional(src, tgt);
        });
    }

    @Test
    public void testBidirectionalPostInjectCallbacks() throws Exception {
        class CallbackBidirectional implements Injectable {
            @InjectSource
            private String value;

            private boolean callbackCalled = false;
            private Injectable receivedParam = null;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            @PostInject
            private void onInject(Injectable param) {
                callbackCalled = true;
                receivedParam = param;
            }

            public boolean isCallbackCalled() {
                return callbackCalled;
            }

            public Injectable getReceivedParam() {
                return receivedParam;
            }
        }

        CallbackBidirectional obj1 = new CallbackBidirectional();
        CallbackBidirectional obj2 = new CallbackBidirectional();

        obj1.setValue("from obj1");
        obj2.setValue("from obj2");

        Injectable.injectBidirectional(obj1, obj2);

        assertTrue(obj1.isCallbackCalled());
        assertTrue(obj2.isCallbackCalled());
        assertSame(obj2, obj1.getReceivedParam());
        assertSame(obj1, obj2.getReceivedParam());
    }

    @Test
    public void testNonMatchingFieldsAreIgnored() throws Exception {
        class PartialSource implements Injectable {
            @InjectSource
            private String matchingField;

            private String ignoredField;

            public void setMatchingField(String value) {
                this.matchingField = value;
            }

            public void setIgnoredField(String value) {
                this.ignoredField = value;
            }
        }

        class PartialTarget implements Injectable {
            @InjectTarget
            private String matchingField;

            @InjectTarget(required = false)
            private String otherTargetField;

            public String getMatchingField() {
                return matchingField;
            }

            public String getOtherTargetField() {
                return otherTargetField;
            }
        }

        PartialSource src = new PartialSource();
        src.setMatchingField("matched value");
        src.setIgnoredField("ignored value");

        PartialTarget tgt = new PartialTarget();

        assertDoesNotThrow(() -> {
            Injectable.injectUnidirectional(src, tgt);
        });

        assertEquals("matched value", tgt.getMatchingField());
        assertNull(tgt.getOtherTargetField());
    }

    @Test
    public void testMultiplePostInjectMethods() throws Exception {
        class MultiCallbackTarget implements Injectable {
            @InjectTarget
            private String value;

            private int callback1Count = 0;
            private int callback2Count = 0;

            @PostInject
            private void callback1() {
                callback1Count++;
            }

            @PostInject
            private void callback2() {
                callback2Count++;
            }

            public String getValue() {
                return value;
            }

            public int getCallback1Count() {
                return callback1Count;
            }

            public int getCallback2Count() {
                return callback2Count;
            }
        }

        class SimpleSource implements Injectable {
            @InjectSource
            private String value;

            public void setValue(String value) {
                this.value = value;
            }
        }

        SimpleSource src = new SimpleSource();
        src.setValue("test");

        MultiCallbackTarget tgt = new MultiCallbackTarget();

        Injectable.injectUnidirectional(src, tgt);

        assertEquals(1, tgt.getCallback1Count());
        assertEquals(1, tgt.getCallback2Count());
    }

    @Test
    public void testSourceWithEmptyTargetNameUsesFieldName() throws Exception {
        class SourceWithEmptyTarget implements Injectable {
            @InjectSource(target = "")
            private String sourceField;

            public String getSourceField() {
                return sourceField;
            }

            public void setSourceField(String value) {
                this.sourceField = value;
            }
        }

        class TargetWithMatchingName implements Injectable {
            @InjectTarget
            private String sourceField;

            public String getSourceField() {
                return sourceField;
            }
        }

        SourceWithEmptyTarget src = new SourceWithEmptyTarget();
        src.setSourceField("test value");

        TargetWithMatchingName tgt = new TargetWithMatchingName();

        Injectable.injectUnidirectional(src, tgt);

        assertEquals("test value", tgt.getSourceField());
    }

    @Test
    public void testUnidirectionalPostInjectOnlyOnTarget() throws Exception {
        class SourceWithCallback implements Injectable {
            @InjectSource
            private String value;

            private boolean callbackCalled = false;

            @PostInject
            private void onInject() {
                callbackCalled = true;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public boolean isCallbackCalled() {
                return callbackCalled;
            }
        }

        class SimpleTarget implements Injectable {
            @InjectTarget
            private String value;

            public String getValue() {
                return value;
            }
        }

        SourceWithCallback src = new SourceWithCallback();
        src.setValue("test");

        SimpleTarget tgt = new SimpleTarget();

        Injectable.injectUnidirectional(src, tgt);

        assertTrue(src.isCallbackCalled());
    }

    private static class TestSource implements Injectable {
        @InjectSource
        private String stringValue;

        @InjectSource
        private Integer intValue;

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public Integer getIntValue() {
            return intValue;
        }

        public void setIntValue(Integer intValue) {
            this.intValue = intValue;
        }
    }

    private static class TestTarget implements Injectable {
        @InjectTarget
        private String stringValue;

        @InjectTarget
        private Integer intValue;

        public String getStringValue() {
            return stringValue;
        }

        public Integer getIntValue() {
            return intValue;
        }
    }

    private static class TestBidirectional implements Injectable {
        @InjectSource(target = "field1")
        @InjectTarget
        private String field1;

        @InjectSource(target = "field2")
        @InjectTarget
        private String field2;

        public String getValue1() {
            return field1;
        }

        public String getValue2() {
            return field2;
        }

        public void setValue1(String value1) {
            this.field1 = value1;
        }

        public void setValue2(String value2) {
            this.field2 = value2;
        }
    }
}
