package zserio.runtime.service;

import zserio.runtime.io.Writer;

/** Data abstraction to be sent or to be received in all Zserio services. */
public interface ServiceData<T extends Writer>
{
    /**
     * Gets the Zserio object which represents the request, if available.
     *
     * @return The Zserio object or null.
     */
    public T getZserioObject();

    /**
     * Gets the data which represent the request.
     *
     * @return The request data.
     */
    public byte[] getByteArray();
}
