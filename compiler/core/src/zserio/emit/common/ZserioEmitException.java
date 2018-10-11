package zserio.emit.common;

/**
 * Exception that can be thrown from all extensions while emitting zserio.
 */
public class ZserioEmitException extends Exception
{
    /**
     * Constructs a new ZserioEmitException with the specified detailed message.
     *
     * @param message Detailed message.
     */
    public ZserioEmitException(String message)
    {
        super(message);
    }

    private static final long serialVersionUID = 1L;
}
