package zserio.runtime.service;

import zserio.runtime.ZserioError;

/**
 * Exception thrown when a call of a service method fails.
 */
public final class ServiceException extends ZserioError
{
    /**
     * Constructor.
     *
     * @param errorMessage  Description of the service method call failure.
     */
    public ServiceException(String errorMessage)
    {
        super(errorMessage);
    }

    private static final long serialVersionUID = 1L;
}
