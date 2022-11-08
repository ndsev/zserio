package zserio.runtime.service;

import zserio.runtime.io.Writer;

/** Data abstraction to be sent or to be received in all Zserio services. */
public class ServiceData<T extends Writer>
{
    /**
     * Constructor.
     *
     * @param zserioObject Zserio object from which to create service data.
     */
    public ServiceData(T zserioObject)
    {
        this.zserioObject = zserioObject;
        this.byteArray = null;
    }

    /**
     * Gets the data which represent the request.
     *
     * @return The request data which are created by serialization of Zserio object.
     */
    public byte[] getByteArray()
    {
        if (byteArray == null)
            byteArray = zserio.runtime.io.SerializeUtil.serializeToBytes(zserioObject);

        return byteArray;
    }

    /**
     * Gets the Zserio object which represents the request.
     *
     * @return The Zserio object.
     */
    public T getZserioObject()
    {
        return zserioObject;
    }

    private final T zserioObject;
    private byte[] byteArray;
};
