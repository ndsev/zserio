package zserio.extension.common;

/**
 * Exception that can be thrown from all extensions while walking Zserio tree.
 */
public class ZserioExtensionException extends Exception
{
    /**
     * Constructs a new ZserioExtensionException with the specified detailed message.
     *
     * @param message Detailed message.
     */
    public ZserioExtensionException(String message)
    {
        super(message);
    }

    private static final long serialVersionUID = 1L;
}
