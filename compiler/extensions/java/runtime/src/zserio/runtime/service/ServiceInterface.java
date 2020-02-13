package zserio.runtime.service;

import zserio.runtime.ZserioError;

/** Generic interface for all Zserio services. */
public interface ServiceInterface
{
    /**
     * Calls method with the given name synchronously
     *
     * @param methodName    Name of the service method to call.
     * @param requestData   Request data to be passed to the method.
     * @param context       Context specific for particular service.
     *
     * @return Response data.
     *
     * @throws ZserioError if serialization or deserialization fails.
     * @throws ServiceException if the call fails.
     */
    public byte[] callMethod(String methodName, byte[] requestData, Object context)
            throws ZserioError;
};
