package zserio.runtime.json;

import java.io.Reader;
import java.io.IOException;

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
        lineNumber = 1;
        columnNumber = 1;
        tokenColumnNumber  = 1;
        pos = 0;
        setToken(content.isEmpty() ? JsonToken.END_OF_FILE : JsonToken.BEGIN_OF_FILE, null);
        decoderResult = null;
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
                {
                    tokenColumnNumber = columnNumber;
                }
                else
                {
                    // stream is finished but last token is not EOF => value must be at the end
                    setTokenValue();
                }

                return token;
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
     * Gets line number of the current token.
     *
     * @return Line number.
     */
    public int getLine()
    {
        return lineNumber;
    }

    /**
     * Gets column number of the current token.
     *
     * @return Column number.
     */
    public int getColumn()
    {
        return tokenColumnNumber;
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
            throw new JsonParserError("JsonTokenizer: Read failure!", excpt);
        }
    }

    private boolean decodeNext()
    {
        if (!skipWhitespaces())
            return false;

        tokenColumnNumber = columnNumber;

        final char nextChar = content.charAt(pos);
        switch (nextChar)
        {
        case '{':
            setToken(JsonToken.BEGIN_OBJECT, nextChar);
            setPosition(pos + 1, columnNumber + 1);
            break;

        case '}':
            setToken(JsonToken.END_OBJECT, nextChar);
            setPosition(pos + 1, columnNumber + 1);
            break;

        case '[':
            setToken(JsonToken.BEGIN_ARRAY, nextChar);
            setPosition(pos + 1, columnNumber + 1);
            break;

        case ']':
            setToken(JsonToken.END_ARRAY, nextChar);
            setPosition(pos + 1, columnNumber + 1);
            break;

        case ':':
            setToken(JsonToken.KEY_SEPARATOR, nextChar);
            setPosition(pos + 1, columnNumber + 1);
            break;

        case ',':
            setToken(JsonToken.ITEM_SEPARATOR, nextChar);
            setPosition(pos + 1, columnNumber + 1);
            break;

        default:
            decoderResult = JsonDecoder.decodeValue(content, pos);
            if (pos + decoderResult.getNumReadChars() >= content.length())
                return false; // we are at the end of chunk => try to read more

            setTokenValue();
            break;
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

    private void setTokenValue()
    {
        if (!decoderResult.success())
        {
            throw new JsonParserError("JsonTokenizer:" + lineNumber + ":" + tokenColumnNumber +
                    ": Unknown token!");
        }

        setToken(JsonToken.VALUE, decoderResult.getValue());
        final int numReadChars = decoderResult.getNumReadChars();
        setPosition(pos + numReadChars, columnNumber + numReadChars);
    }

    private static final int MAX_LINE_LEN = 64 * 1024;

    private final Reader reader;
    private final char[] buffer = new char[MAX_LINE_LEN];

    private String content;
    private int lineNumber;
    private int columnNumber;
    private int tokenColumnNumber;
    private int pos;
    private JsonToken token;
    private Object value;
    private JsonDecoder.Result decoderResult;
}
