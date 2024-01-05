package zserio.runtime.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class JsonParserTest
{
    @Test
    public void empty() throws IOException
    {
        final Reader reader = new StringReader("");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        assertTrue(jsonParser.parse());
        assertEquals(0, observer.getReport().size());

        reader.close();
    }

    @Test
    public void oneString() throws IOException
    {
        final Reader reader = new StringReader("\"text\"");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        assertTrue(jsonParser.parse());
        assertEquals(1, observer.getReport().size());
        assertEquals("visitValue: text", observer.getReport().get(0));

        reader.close();
    }

    @Test
    public void twoStrings() throws IOException
    {
        final Reader reader = new StringReader("\"text\"\"second\"");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        assertFalse(jsonParser.parse());
        assertEquals(1, observer.getReport().size());
        assertEquals("visitValue: text", observer.getReport().get(0));
        assertEquals(1, jsonParser.getLine());
        assertEquals(7, jsonParser.getColumn());

        assertTrue(jsonParser.parse());
        assertEquals(2, observer.getReport().size());
        assertEquals("visitValue: second", observer.getReport().get(1));
        assertEquals(1, jsonParser.getLine());
        assertEquals(15, jsonParser.getColumn());

        reader.close();
    }

    @Test
    public void parse() throws IOException
    {
        final Reader reader = new StringReader("{\"array\":\n[\n{\"key1\":\n10, \"key2\":\n\"text\"}, {}]}");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        assertTrue(jsonParser.parse());
        assertEquals(13, observer.getReport().size());
        assertEquals("beginObject", observer.getReport().get(0));
        assertEquals("visitKey: array", observer.getReport().get(1));
        assertEquals("beginArray", observer.getReport().get(2));
        assertEquals("beginObject", observer.getReport().get(3));
        assertEquals("visitKey: key1", observer.getReport().get(4));
        assertEquals("visitValue: 10", observer.getReport().get(5));
        assertEquals("visitKey: key2", observer.getReport().get(6));
        assertEquals("visitValue: text", observer.getReport().get(7));
        assertEquals("endObject", observer.getReport().get(8));
        assertEquals("beginObject", observer.getReport().get(9));
        assertEquals("endObject", observer.getReport().get(10));
        assertEquals("endArray", observer.getReport().get(11));
        assertEquals("endObject", observer.getReport().get(12));

        reader.close();
    }

    @Test
    public void unexpectedObject() throws IOException
    {
        final Reader reader = new StringReader("{\n\n{\n\n");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        final JsonParserError exception = assertThrows(JsonParserError.class, () -> jsonParser.parse());
        assertEquals("JsonParser:3:1: Unexpected token: BEGIN_OBJECT ('{'), expecting END_OBJECT!",
                exception.getMessage());
        assertEquals(1, observer.getReport().size());
        assertEquals("beginObject", observer.getReport().get(0));

        reader.close();
    }

    @Test
    public void unexpectedObjectAfterItemSeparator() throws IOException
    {
        final Reader reader = new StringReader("{\n  \"key\": 10,\n  {\n");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        final JsonParserError exception = assertThrows(JsonParserError.class, () -> jsonParser.parse());
        assertEquals("JsonParser:3:3: Unexpected token: BEGIN_OBJECT ('{'), expecting VALUE!",
                exception.getMessage());
        assertEquals(3, observer.getReport().size());
        assertEquals("beginObject", observer.getReport().get(0));
        assertEquals("visitKey: key", observer.getReport().get(1));
        assertEquals("visitValue: 10", observer.getReport().get(2));

        reader.close();
    }

    @Test
    public void missingObjectItemSeparator() throws IOException
    {
        final Reader reader = new StringReader("{\n\"item1\":\"text\"\n\"item2\":\"text\"\n}");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        final JsonParserError exception = assertThrows(JsonParserError.class, () -> jsonParser.parse());
        assertEquals("JsonParser:3:1: Unexpected token: VALUE ('item2'), expecting END_OBJECT!",
                exception.getMessage());
        assertEquals(3, observer.getReport().size());
        assertEquals("beginObject", observer.getReport().get(0));
        assertEquals("visitKey: item1", observer.getReport().get(1));
        assertEquals("visitValue: text", observer.getReport().get(2));

        reader.close();
    }

    @Test
    public void wrongKeyType() throws IOException
    {
        final Reader reader = new StringReader("{\n10:\"text\"\n}");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        final JsonParserError exception = assertThrows(JsonParserError.class, () -> jsonParser.parse());
        assertEquals("JsonParser:2:1: Key must be a string value!", exception.getMessage());
        assertEquals(1, observer.getReport().size());
        assertEquals("beginObject", observer.getReport().get(0));

        reader.close();
    }

    @Test
    public void unexpectedElementToken() throws IOException
    {
        final Reader reader = new StringReader("{\n\"item\":}");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        final JsonParserError exception = assertThrows(JsonParserError.class, () -> jsonParser.parse());
        assertEquals("JsonParser:2:8: Unexpected token: END_OBJECT ('}'), expecting one of "
                        + "[BEGIN_OBJECT, BEGIN_ARRAY, VALUE]!",
                exception.getMessage());
        assertEquals(2, observer.getReport().size());
        assertEquals("beginObject", observer.getReport().get(0));
        assertEquals("visitKey: item", observer.getReport().get(1));

        reader.close();
    }

    @Test
    public void missingArrayElementSeparator() throws IOException
    {
        final Reader reader = new StringReader("{\n\"array\":\n[10\n20\n]}");
        final DummyObserver observer = new DummyObserver();
        final JsonParser jsonParser = new JsonParser(reader, observer);

        final JsonParserError exception = assertThrows(JsonParserError.class, () -> jsonParser.parse());
        assertEquals(
                "JsonParser:4:1: Unexpected token: VALUE ('20'), expecting END_ARRAY!", exception.getMessage());
        assertEquals(4, observer.getReport().size());
        assertEquals("beginObject", observer.getReport().get(0));
        assertEquals("visitKey: array", observer.getReport().get(1));
        assertEquals("beginArray", observer.getReport().get(2));
        assertEquals("visitValue: 10", observer.getReport().get(3));

        reader.close();
    }

    private static class DummyObserver implements JsonParser.Observer
    {
        public List<String> getReport()
        {
            return report;
        }

        @Override
        public void beginObject()
        {
            report.add("beginObject");
        }

        @Override
        public void endObject()
        {
            report.add("endObject");
        }

        @Override
        public void beginArray()
        {
            report.add("beginArray");
        }

        @Override
        public void endArray()
        {
            report.add("endArray");
        }

        @Override
        public void visitKey(String key)
        {
            report.add("visitKey: " + key);
        }

        @Override
        public void visitValue(Object value)
        {
            report.add("visitValue: " + value.toString());
        }

        private final List<String> report = new ArrayList<String>();
    };
}
