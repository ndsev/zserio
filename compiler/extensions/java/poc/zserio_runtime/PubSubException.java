package zserio_runtime;

import zserio.runtime.ZserioError;

public class PubSubException extends ZserioError
{
    /**
     * Constructor.
     *
     * @param errorMessage  Description of the pubsub error.
     */
    public PubSubException(String errorMessage)
    {
        super(errorMessage);
    }

    private static final long serialVersionUID = 1L;
}
