package zserio.runtime.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void lineColumn() throws IOException
    {
        final Reader reader = new StringReader("\n{\n   \"key\"  \n :\n10}");
        final JsonTokenizer tokenizer = new JsonTokenizer(reader);

        assertEquals(JsonToken.BEGIN_OBJECT, tokenizer.next());
        assertEquals('{', tokenizer.getValue());
        assertEquals(2, tokenizer.getLine());
        assertEquals(1, tokenizer.getColumn());

        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals("key", tokenizer.getValue());
        assertEquals(3, tokenizer.getLine());
        assertEquals(4, tokenizer.getColumn());

        assertEquals(JsonToken.KEY_SEPARATOR, tokenizer.next());
        assertEquals(':', tokenizer.getValue());
        assertEquals(4, tokenizer.getLine());
        assertEquals(2, tokenizer.getColumn());

        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals(BigInteger.valueOf(10), tokenizer.getValue());
        assertEquals(5, tokenizer.getLine());
        assertEquals(1, tokenizer.getColumn());

        assertEquals(JsonToken.END_OBJECT, tokenizer.next());
        assertEquals('}', tokenizer.getValue());
        assertEquals(5, tokenizer.getLine());
        assertEquals(3, tokenizer.getColumn());

        assertEquals(JsonToken.END_OF_FILE, tokenizer.next());
        assertEquals(5, tokenizer.getLine());
        assertEquals(4, tokenizer.getColumn());

        reader.close();
    }

    @Test
    public void longInputSplitInNumber()
    {
        final StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n"); // 2 chars
        for (int i = 0; i < 4000; ++i) // 20 x 4000 > 65534 to check reading by chunks
        {
            // BUFFER_SIZE is 65536, thus 65534 % 20 gives position within the string below
            // where the buffer will be split => 14, which is somewhere in the middle of the number
            //                 |->            <-|
            jsonBuilder.append("  \"key\": 100000000,\n"); // 20 chars
        }
        jsonBuilder.append("  \"key\": 100000000\n");
        jsonBuilder.append('}');

        final Reader reader = new StringReader(jsonBuilder.toString());
        final JsonTokenizer tokenizer = new JsonTokenizer(reader);

        assertEquals(JsonToken.BEGIN_OBJECT, tokenizer.next());
        assertEquals('{', tokenizer.getValue());
        assertEquals(1, tokenizer.getLine());
        assertEquals(1, tokenizer.getColumn());

        int i = 0;
        for (; i < 4000; ++i)
        {
            assertEquals(JsonToken.VALUE, tokenizer.next());
            assertEquals("key", tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(3, tokenizer.getColumn());

            assertEquals(JsonToken.KEY_SEPARATOR, tokenizer.next());
            assertEquals(':', tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(8, tokenizer.getColumn());

            assertEquals(JsonToken.VALUE, tokenizer.next());
            assertEquals(BigInteger.valueOf(100000000), tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(10, tokenizer.getColumn());

            assertEquals(JsonToken.ITEM_SEPARATOR, tokenizer.next());
            assertEquals(',', tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(19, tokenizer.getColumn());
        }

        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals("key", tokenizer.getValue());
        assertEquals(1 + i + 1, tokenizer.getLine());
        assertEquals(3, tokenizer.getColumn());

        assertEquals(JsonToken.KEY_SEPARATOR, tokenizer.next());
        assertEquals(':', tokenizer.getValue());
        assertEquals(1 + i + 1, tokenizer.getLine());
        assertEquals(8, tokenizer.getColumn());

        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals(BigInteger.valueOf(100000000), tokenizer.getValue());
        assertEquals(1 + i + 1, tokenizer.getLine());
        assertEquals(10, tokenizer.getColumn());

        assertEquals(JsonToken.END_OBJECT, tokenizer.next());
        assertEquals(1 + i + 2, tokenizer.getLine());
        assertEquals(1, tokenizer.getColumn());
    }

    @Test
    public void longInputSplitInString()
    {
        final StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n"); // 2 chars
        for (int i = 0; i < 4000; ++i) // 20 x 4000 > 65534 to check reading by chunks
        {
            // BUFFER_SIZE is 65536, thus 65534 % 20 gives position within the string below
            // where the buffer will be split => 14, which is somewhere in the middle of the number
            //                 |->             <-|
            jsonBuilder.append("  \"key\": \"1000000\",\n"); // 20 chars
        }
        jsonBuilder.append("  \"key\": \"1000000\"\n");
        jsonBuilder.append('}');

        final Reader reader = new StringReader(jsonBuilder.toString());
        final JsonTokenizer tokenizer = new JsonTokenizer(reader);

        assertEquals(JsonToken.BEGIN_OBJECT, tokenizer.next());
        assertEquals('{', tokenizer.getValue());
        assertEquals(1, tokenizer.getLine());
        assertEquals(1, tokenizer.getColumn());

        int i = 0;
        for (; i < 4000; ++i)
        {
            assertEquals(JsonToken.VALUE, tokenizer.next());
            assertEquals("key", tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(3, tokenizer.getColumn());

            assertEquals(JsonToken.KEY_SEPARATOR, tokenizer.next());
            assertEquals(':', tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(8, tokenizer.getColumn());

            assertEquals(JsonToken.VALUE, tokenizer.next());
            assertEquals("1000000", tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(10, tokenizer.getColumn());

            assertEquals(JsonToken.ITEM_SEPARATOR, tokenizer.next());
            assertEquals(',', tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(19, tokenizer.getColumn());
        }

        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals("key", tokenizer.getValue());
        assertEquals(1 + i + 1, tokenizer.getLine());
        assertEquals(3, tokenizer.getColumn());

        assertEquals(JsonToken.KEY_SEPARATOR, tokenizer.next());
        assertEquals(':', tokenizer.getValue());
        assertEquals(1 + i + 1, tokenizer.getLine());
        assertEquals(8, tokenizer.getColumn());

        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals("1000000", tokenizer.getValue());
        assertEquals(1 + i + 1, tokenizer.getLine());
        assertEquals(10, tokenizer.getColumn());

        assertEquals(JsonToken.END_OBJECT, tokenizer.next());
        assertEquals(1 + i + 2, tokenizer.getLine());
        assertEquals(1, tokenizer.getColumn());
    }

    @Test
    public void longInputSplitInDoubleAfterE()
    {
        final StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n"); // 2 chars
        for (int i = 0; i < 4000; ++i) // 20 x 4000 > 65534 to check reading by chunks
        {
            // BUFFER_SIZE is 65536, thus 65534 % 20 gives position within the string below
            // where the buffer will be split => 14, which is somewhere in the middle of the number
            //                 |->            <-|
            jsonBuilder.append("  \"key\":    1e5   ,\n"); // 20 chars
        }
        jsonBuilder.append("  \"key\":    1e5  \n");
        jsonBuilder.append('}');

        final Reader reader = new StringReader(jsonBuilder.toString());
        final JsonTokenizer tokenizer = new JsonTokenizer(reader);

        assertEquals(JsonToken.BEGIN_OBJECT, tokenizer.next());
        assertEquals('{', tokenizer.getValue());
        assertEquals(1, tokenizer.getLine());
        assertEquals(1, tokenizer.getColumn());

        int i = 0;
        for (; i < 4000; ++i)
        {
            assertEquals(JsonToken.VALUE, tokenizer.next());
            assertEquals("key", tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(3, tokenizer.getColumn());

            assertEquals(JsonToken.KEY_SEPARATOR, tokenizer.next());
            assertEquals(':', tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(8, tokenizer.getColumn());

            assertEquals(JsonToken.VALUE, tokenizer.next());
            assertEquals(1e5, tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(13, tokenizer.getColumn());

            assertEquals(JsonToken.ITEM_SEPARATOR, tokenizer.next());
            assertEquals(',', tokenizer.getValue());
            assertEquals(1 + i + 1, tokenizer.getLine());
            assertEquals(19, tokenizer.getColumn());
        }

        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals("key", tokenizer.getValue());
        assertEquals(1 + i + 1, tokenizer.getLine());
        assertEquals(3, tokenizer.getColumn());

        assertEquals(JsonToken.KEY_SEPARATOR, tokenizer.next());
        assertEquals(':', tokenizer.getValue());
        assertEquals(1 + i + 1, tokenizer.getLine());
        assertEquals(8, tokenizer.getColumn());

        assertEquals(JsonToken.VALUE, tokenizer.next());
        assertEquals(1e5, tokenizer.getValue());
        assertEquals(1 + i + 1, tokenizer.getLine());
        assertEquals(13, tokenizer.getColumn());

        assertEquals(JsonToken.END_OBJECT, tokenizer.next());
        assertEquals(1 + i + 2, tokenizer.getLine());
        assertEquals(1, tokenizer.getColumn());
    }

    @Test
    public void unknownToken() throws IOException
    {
        final Reader reader = new StringReader("\\\n");
        final JsonTokenizer tokenizer = new JsonTokenizer(reader);
        final JsonParserError exception = assertThrows(JsonParserError.class, () -> tokenizer.next());
        assertEquals("JsonTokenizer:1:1: Unknown token!", exception.getMessage());

        reader.close();
    }
}
