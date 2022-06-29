package zserio.runtime.json;

import zserio.runtime.ZserioError;

/**
 * Provides type of exceptions thrown from JSON parser.
 */
public class JsonParserError extends ZserioError
{
    /**
     * Constructs an empty JSON parser error object.
     */
    public JsonParserError()
    {}

    /**
     * Constructs a new JSON parser error with the given message.
     *
     * @param msg Error message to create from.
     */
    public JsonParserError(final String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new JSON parser error with the given message and throwable object.
     *
     * @param msg Error message to create from.
     * @param throwable Throwable object to create from.
     */
    public JsonParserError(final String msg, final Throwable throwable)
    {
        super(msg, throwable);
    }

    /**
     * Construct a new JSON parser error with the given throwable object.
     *
     * @param throwable Throwable object to create from.
     */
    public JsonParserError(final Throwable throwable)
    {
        super(throwable);
    }

   private static final long serialVersionUID = 921445202560692333L;
}
