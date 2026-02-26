package org.update4j.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestMapMapper {

    @Test
    public void testReadWithValidXml() throws IOException {
        String xml = "<map>\n" +
                "    <item key=\"key1\" value=\"value1\"/>\n" +
                "    <item key=\"key2\" value=\"value2\"/>\n" +
                "</map>";

        Map<String, String> result = MapMapper.read(new StringReader(xml));

        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    public void testReadWithCustomRootName() throws IOException {
        String xml = "<properties>\n" +
                "    <item key=\"prop1\" value=\"value1\"/>\n" +
                "</properties>";

        Map<String, String> result = MapMapper.read(new StringReader(xml), "properties");

        assertEquals(1, result.size());
        assertEquals("value1", result.get("prop1"));
    }

    @Test
    public void testReadWithEmptyMap() throws IOException {
        String xml = "<map/>";

        Map<String, String> result = MapMapper.read(new StringReader(xml));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testReadWithEmptyMapFullSyntax() throws IOException {
        String xml = "<map></map>";

        Map<String, String> result = MapMapper.read(new StringReader(xml));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testReadIgnoresNullKey() throws IOException {
        String xml = "<map>\n" +
                "    <item key=\"validKey\" value=\"value\"/>\n" +
                "    <item value=\"noKey\"/>\n" +
                "</map>";

        Map<String, String> result = MapMapper.read(new StringReader(xml));

        assertEquals(1, result.size());
        assertEquals("value", result.get("validKey"));
    }

    @Test
    public void testReadIgnoresNullValue() throws IOException {
        String xml = "<map>\n" +
                "    <item key=\"validKey\" value=\"value\"/>\n" +
                "    <item key=\"noValue\"/>\n" +
                "</map>";

        Map<String, String> result = MapMapper.read(new StringReader(xml));

        assertEquals(1, result.size());
        assertEquals("value", result.get("validKey"));
    }

    @Test
    public void testReadThrowsExceptionForWrongRootName() {
        String xml = "<different>";

        assertThrows(Exception.class, () -> {
            MapMapper.read(new StringReader(xml));
        });
    }

    @Test
    public void testWriteWithMap() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        StringWriter writer = new StringWriter();
        MapMapper.write(writer, map);

        String result = writer.toString();
        assertTrue(result.contains("<map>"));
        assertTrue(result.contains("</map>"));
        assertTrue(result.contains("key=\"key1\""));
        assertTrue(result.contains("value=\"value1\""));
    }

    @Test
    public void testWriteWithCustomRootName() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("prop1", "value1");

        StringWriter writer = new StringWriter();
        MapMapper.write(writer, map, "properties");

        String result = writer.toString();
        assertTrue(result.contains("<properties>"));
        assertTrue(result.contains("</properties>"));
    }

    @Test
    public void testWriteWithEmptyMap() throws IOException {
        Map<String, String> map = new HashMap<>();

        StringWriter writer = new StringWriter();
        MapMapper.write(writer, map);

        String result = writer.toString();
        assertEquals("<map/>", result);
    }

    @Test
    public void testWriteEscapesXmlCharacters() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("key<tag>", "value&special");

        StringWriter writer = new StringWriter();
        MapMapper.write(writer, map);

        String result = writer.toString();
        assertTrue(result.contains("&lt;"));
        assertTrue(result.contains("&gt;"));
        assertTrue(result.contains("&amp;"));
    }

    @Test
    public void testWriteIgnoresNullKey() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("validKey", "value");
        map.put(null, "nullKey");
        map.put("anotherValid", "value2");

        StringWriter writer = new StringWriter();
        MapMapper.write(writer, map);

        String result = writer.toString();
        assertTrue(result.contains("key=\"validKey\""));
        assertTrue(result.contains("key=\"anotherValid\""));
        assertFalse(result.contains("null"));
    }

    @Test
    public void testWriteIgnoresNullValue() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("validKey", "value");
        map.put("nullValue", null);
        map.put("anotherValid", "value2");

        StringWriter writer = new StringWriter();
        MapMapper.write(writer, map);

        String result = writer.toString();
        assertTrue(result.contains("key=\"validKey\""));
        assertTrue(result.contains("key=\"anotherValid\""));
        assertFalse(result.contains("nullValue"));
    }

    @Test
    public void testReadWriteRoundTrip() throws IOException {
        Map<String, String> original = new HashMap<>();
        original.put("key1", "value1");
        original.put("key2", "value2");

        StringWriter writer = new StringWriter();
        MapMapper.write(writer, original);

        Map<String, String> result = MapMapper.read(new StringReader(writer.toString()));

        assertEquals(original, result);
    }

    @Test
    public void testParseWithDifferentNodeName() {
        String xml = "<different>\n" +
                "    <item key=\"key1\" value=\"value1\"/>\n" +
                "</different>";

        assertThrows(Exception.class, () -> {
            MapMapper.read(new StringReader(xml));
        });
    }

    @Test
    public void testReadMultipleItems() throws IOException {
        String xml = "<map>\n" +
                "    <item key=\"a\" value=\"1\"/>\n" +
                "    <item key=\"b\" value=\"2\"/>\n" +
                "    <item key=\"c\" value=\"3\"/>\n" +
                "</map>";

        Map<String, String> result = MapMapper.read(new StringReader(xml));

        assertEquals(3, result.size());
        assertEquals("1", result.get("a"));
        assertEquals("2", result.get("b"));
        assertEquals("3", result.get("c"));
    }

    @Test
    public void testWriteMultipleItems() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");

        StringWriter writer = new StringWriter();
        MapMapper.write(writer, map);

        String result = writer.toString();
        assertTrue(result.contains("key=\"a\""));
        assertTrue(result.contains("key=\"b\""));
        assertTrue(result.contains("key=\"c\""));
    }

    @Test
    public void testReadWithWhitespaceInValues() throws IOException {
        String xml = "<map>\n" +
                "    <item key=\"key\" value=\"  value with spaces  \"/>\n" +
                "</map>";

        Map<String, String> result = MapMapper.read(new StringReader(xml));

        assertEquals("  value with spaces  ", result.get("key"));
    }

    @Test
    public void testWriteWithSpecialCharacters() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value with 'quotes' and \"double quotes\"");

        StringWriter writer = new StringWriter();
        MapMapper.write(writer, map);

        String result = writer.toString();
        assertTrue(result.contains("&apos;"));
        assertTrue(result.contains("&quot;"));
    }
}
