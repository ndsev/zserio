package zserio.runtime.pubsub;

import zserio.runtime.ZserioError;

/**
 * Exception thrown when an error in Pub/Sub occurs.
 */
public class PubsubException extends ZserioError
{
    /**
     * Constructor.
     *
     * @param errorMessage  Description of the Pub/Sub failure.
     */
    public PubsubException(String errorMessage)
    {
        super(errorMessage);
    }

    private static final long serialVersionUID = 1L;
}
