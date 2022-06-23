package zserio.runtime.json;

import java.io.Reader;

import zserio.runtime.ZserioError;

/**
 * Json parser.
 *
 * Parses the json on the fly and calls an observer.
 */
class JsonParser
{
    /**
     * Constructor.
     *
     * @param reader Text stream to parse.
     * @param observer Json reader observer.
     */
    public JsonParser(Reader reader, Observer observer)
    {
        this.tokenizer = new JsonTokenizer(reader);
        this.observer = observer;
    }

    /**
     * Parses single JSON element from the text stream.
     *
     * @return True when end-of-file is reached, False otherwise (i.e. another JSON element is present).
     */
    public boolean parse()
    {
        if (tokenizer.getToken() == JsonToken.BEGIN_OF_FILE)
            tokenizer.next();

        if (tokenizer.getToken() == JsonToken.END_OF_FILE)
            return true;

        parseElement();

        return tokenizer.getToken() == JsonToken.END_OF_FILE;
    }

    /**
     * Gets current line number.
     *
     * @return Line number.
     */
    public int getLine()
    {
        return tokenizer.getLine();
    }

    /**
     * Json parser observer.
     */
    static interface Observer
    {
        /**
         * Called when a JSON object begins - i.e. on '{'.
         */
        public void beginObject();

        /**
         * Called when a JSON object ends - i.e. on '}'.
         */
        public void endObject();

        /**
         * Called when a JSON array begins - i.e. on '['.
         */
        public void beginArray();

        /**
         * Called when a JSON array ends - i.e. on ']'.
         */
        public void endArray();

        /**
         * Called on a JSON key.
         *
         * @param key Key value.
         */
        public void visitKey(String key);

        /**
         * Called on a JSON value.
         *
         * @param value JSON value.
         */
        public void visitValue(Object value);
    }

    private void parseElement()
    {
        final JsonToken token = tokenizer.getToken();
        if (token == JsonToken.BEGIN_ARRAY)
            parseArray();
        else if (token == JsonToken.BEGIN_OBJECT)
            parseObject();
        else if (token == JsonToken.VALUE)
            parseValue();
        else
            throwUnexpectedToken(ELEMENT_TOKENS);
    }

    private void parseArray()
    {
        observer.beginArray();
        final JsonToken token = tokenizer.next();
        for (JsonToken elementToken : ELEMENT_TOKENS)
        {
            if (token == elementToken)
            {
                parseElements();
                break;
            }
        }

        consumeToken(JsonToken.END_ARRAY);
        observer.endArray();
    }

    private void parseElements()
    {
        parseElement();
        while (tokenizer.getToken() == JsonToken.ITEM_SEPARATOR)
        {
            tokenizer.next();
            parseElement();
        }
    }

    private void parseObject()
    {
        observer.beginObject();
        final JsonToken token = tokenizer.next();
        if (token == JsonToken.VALUE)
            parseMembers();

        consumeToken(JsonToken.END_OBJECT);
        observer.endObject();
    }

    private void parseMembers()
    {
        parseMember();
        while (tokenizer.getToken() == JsonToken.ITEM_SEPARATOR)
        {
            tokenizer.next();
            parseMember();
        }
    }

    private void parseMember()
    {
        checkToken(JsonToken.VALUE);
        final Object key = tokenizer.getValue();
        if (!(key instanceof String))
        {
            throw new ZserioError("JsonParser:" + tokenizer.getLine() + ":" + tokenizer.getColumn() +
                    ": Key must be a string value!");
        }
        observer.visitKey((String)key);
        tokenizer.next();

        consumeToken(JsonToken.KEY_SEPARATOR);

        parseElement();
    }

    private void parseValue()
    {
        observer.visitValue(tokenizer.getValue());
        tokenizer.next();
    }

    private void consumeToken(JsonToken expectedToken)
    {
        checkToken(expectedToken);
        tokenizer.next();
    }

    private void checkToken(JsonToken expectedToken)
    {
        if (tokenizer.getToken() != expectedToken)
            throwUnexpectedToken(new JsonToken[] {expectedToken});
    }

    private void throwUnexpectedToken(JsonToken[] expecting)
    {
        String msg = "JsonParser:" + tokenizer.getLine() + ":" + tokenizer.getColumn() +
                ": Unexpected token: " + tokenizer.getToken();
        final Object value = tokenizer.getValue();
        if (value != null)
        {
            msg += " ('" + value + "')";
        }
        if (expecting.length == 1)
        {
            msg += ", expecting " + expecting[0] + "!";
        }
        else
        {
            final StringBuilder builder = new StringBuilder();
            int i = 0;
            for (; i < expecting.length - 1; ++i)
            {
                builder.append(expecting[i].toString());
                builder.append(", ");
            }
            builder.append(expecting[i].toString());
            msg += ", expecting one of [" + builder.toString() + "]!";
        }

        throw new ZserioError(msg);
    }

    static JsonToken[] ELEMENT_TOKENS =
            new JsonToken[] {JsonToken.BEGIN_OBJECT, JsonToken.BEGIN_ARRAY, JsonToken.VALUE};

    private final JsonTokenizer tokenizer;
    private final Observer observer;
}
