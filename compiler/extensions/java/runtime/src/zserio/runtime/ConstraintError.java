package zserio.runtime;

/**
 * Provides type of exceptions thrown from generated code by Zserio whenever checking of constraint fails.
 */
public final class ConstraintError extends ZserioError
{
    /**
     * Constructs an empty constraint error object.
     */
    public ConstraintError()
    {}

    /**
     * Constructs a new constraint error with the given message.
     *
     * @param msg Error message to create from.
     */
    public ConstraintError(final String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new constraint error with the given message and throwable object.
     *
     * @param msg       Error message to create from.
     * @param throwable Throwable object to create from.
     */
    public ConstraintError(final String msg, final Throwable throwable)
    {
        super(msg, throwable);
    }

    /**
     * Construct a new data constraint with the given throwable object.
     *
     * @param throwable Throwable object to create from.
     */
    public ConstraintError(final Throwable throwable)
    {
        super(throwable);
    }

   private static final long serialVersionUID = 921445202560692444L;
}
