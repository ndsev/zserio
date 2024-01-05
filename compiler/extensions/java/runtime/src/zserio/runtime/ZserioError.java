package zserio.runtime;

/**
 * Provides type for exceptions thrown from generated code by Zserio.
 */
public class ZserioError extends RuntimeException
{
    /**
     * Constructs an empty data script error object.
     */
    public ZserioError()
    {}

    /**
     * Constructs a new data script error with the given message.
     *
     * @param msg Error message to create from.
     */
    public ZserioError(final String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new data script error with the given message and throwable object.
     *
     * @param msg       Error message to create from.
     * @param throwable Throwable object to create from.
     */
    public ZserioError(final String msg, final Throwable throwable)
    {
        super(msg, throwable);
    }

    /**
     * Construct a new data script error with the given throwable object.
     *
     * @param throwable Throwable object to create from.
     */
    public ZserioError(final Throwable throwable)
    {
        super(throwable);
    }

    private static final long serialVersionUID = 921445202560692775L;
}
