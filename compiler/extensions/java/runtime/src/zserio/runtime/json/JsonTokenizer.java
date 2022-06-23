package zserio.runtime.json;

import java.io.Reader;
import java.io.IOException;

import zserio.runtime.ZserioError;

/**
 * Json Tokenizer used by Json Parser.
 */
class JsonTokenizer
{
    /**
     * Constructor.
     *
     * @param reader Text stream to tokenize.
     */
    public JsonTokenizer(Reader reader)
    {
        this.reader = reader;

        content = readContent();
        if (content.isEmpty())
        {
            lineNumber = 0;
            setToken(JsonToken.END_OF_FILE, null);
            setPosition(0, 0);
        }
        else
        {
            lineNumber = 1;
            setToken(JsonToken.BEGIN_OF_FILE, null);
            setPosition(0, 1);
        }
    }

    /**
     * Moves to next token.
     *
     * @return Next token.
     */
    public JsonToken next()
    {
        while (!decodeNext())
        {
            String newContent = readContent();
            if (newContent.isEmpty())
            {
                if (token == JsonToken.END_OF_FILE)
                    return token;

                // stream is not finished by whitespace => emulate new line
                newContent = "\n";
            }

            content = content.substring(pos) + newContent;
            pos = 0;
        }

        return token;
    }

    /**
     * Gets current token.
     *
     * @return Current token.
     */
    public JsonToken getToken()
    {
        return token;
    }

    /**
     * Gets current value.
     *
     * @return Current value.
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Gets current line number.
     *
     * @return Line number.
     */
    public int getLine()
    {
        return lineNumber;
    }

    /**
     * Gets current column number.
     *
     * @return Column number.
     */
    public int getColumn()
    {
        return columnNumber;
    }

    String readContent()
    {
        try
        {
            final int count = reader.read(buffer);
            if (count == -1)
                return "";

            return new String(buffer, 0, count);
        }
        catch (IOException excpt)
        {
            throw new ZserioError("JsonTokenizer: Read failure!", excpt);
        }
    }

    private boolean decodeNext()
    {
        if (!skipWhitespaces())
            return false;

        final char nextChar = content.charAt(pos);
        if (nextChar == '{')
        {
            setToken(JsonToken.BEGIN_OBJECT, nextChar);
            setPosition(pos + 1, columnNumber + 1);
        }
        else if (nextChar == '}')
        {
            setToken(JsonToken.END_OBJECT, nextChar);
            setPosition(pos + 1, columnNumber + 1);
        }
        else if (nextChar == '[')
        {
            setToken(JsonToken.BEGIN_ARRAY, nextChar);
            setPosition(pos + 1, columnNumber + 1);
        }
        else if (nextChar == ']')
        {
            setToken(JsonToken.END_ARRAY, nextChar);
            setPosition(pos + 1, columnNumber + 1);
        }
        else if (nextChar == ':')
        {
            setToken(JsonToken.KEY_SEPARATOR, nextChar);
            setPosition(pos + 1, columnNumber + 1);
        }
        else if (nextChar == ',')
        {
            setToken(JsonToken.ITEM_SEPARATOR, nextChar);
            setPosition(pos + 1, columnNumber + 1);
        }
        else
        {
            final JsonDecoder.Result result = JsonDecoder.decodeValue(content, pos);
            final int numReadChars = result.getNumReadChars();

            if (pos + numReadChars >= content.length())
                return false; // we are at the end of chunk => read more

            if (!result.success())
                throw new ZserioError("JsonTokenizer:" + lineNumber + ":" + columnNumber + ": Unknown token!");

            setToken(JsonToken.VALUE, result.getObject());
            setPosition(pos + numReadChars, columnNumber + numReadChars);
        }

        return true;
    }

    private boolean skipWhitespaces()
    {
        while (true)
        {
            if (pos >= content.length())
            {
                setToken(JsonToken.END_OF_FILE, null);
                return false;
            }

            final char nextChar = content.charAt(pos);
            if (nextChar == ' ' || nextChar == '\t')
            {
                setPosition(pos + 1, columnNumber + 1);
            }
            else if (nextChar == '\n')
            {
                lineNumber++;
                setPosition(pos + 1, 1);
            }
            else if (nextChar == '\r')
            {
                if (pos + 1 >= content.length())
                {
                    setToken(JsonToken.END_OF_FILE, null);
                    return false;
                }
                final char nextNextChar = content.charAt(pos + 1);
                lineNumber++;
                setPosition(pos + ((nextNextChar == '\n') ? 2 : 1), 1);
            }
            else
            {
                break;
            }
        }

        return true;
    }

    private void setToken(JsonToken newToken, Object newValue)
    {
        token = newToken;
        value = newValue;
    }

    private void setPosition(int newPos, int newColumnNumber)
    {
        pos = newPos;
        columnNumber = newColumnNumber;
    }

    private static final int MAX_LINE_LEN = 64 * 1024;

    private final Reader reader;
    private final char[] buffer = new char[MAX_LINE_LEN];

    private String content;
    private int lineNumber;
    private int columnNumber;
    private int pos;
    private JsonToken token;
    private Object value;
}
