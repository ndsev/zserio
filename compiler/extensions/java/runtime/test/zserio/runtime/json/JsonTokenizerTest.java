package zserio.runtime.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class JsonTokenizerTest
{
    @Test
    public void tokens() throws IOException
    {
        final Reader reader = new StringReader("{\"array\":\n[\n{\"key\":\n10}]}");
        final JsonTokenizer tokenizer = new JsonTokenizer(reader);

        assertEquals(JsonToken.BEGIN_OBJECT, tokenizer.next());
        assertEquals('{', tokenizer.getValue());
        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals("array", tokenizer.getValue());
        assertEquals(JsonToken.KEY_SEPARATOR, tokenizer.next());
        assertEquals(':', tokenizer.getValue());
        assertEquals(JsonToken.BEGIN_ARRAY, tokenizer.next());
        assertEquals('[', tokenizer.getValue());
        assertEquals(JsonToken.BEGIN_OBJECT, tokenizer.next());
        assertEquals('{', tokenizer.getValue());
        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals("key", tokenizer.getValue());
        assertEquals(JsonToken.KEY_SEPARATOR, tokenizer.next());
        assertEquals(':', tokenizer.getValue());
        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals(BigInteger.valueOf(10), tokenizer.getValue());
        assertEquals(JsonToken.END_OBJECT, tokenizer.next());
        assertEquals('}', tokenizer.getValue());
        assertEquals(JsonToken.END_ARRAY, tokenizer.next());
        assertEquals(']', tokenizer.getValue());
        assertEquals(JsonToken.END_OBJECT, tokenizer.next());
        assertEquals('}', tokenizer.getValue());
        assertEquals(JsonToken.END_OF_FILE, tokenizer.next());

        reader.close();
    }

    // TODO[mikir] new test for all public methods...
}
