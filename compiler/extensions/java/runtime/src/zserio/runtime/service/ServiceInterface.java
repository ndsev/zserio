package zserio.runtime.service;

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
     */
    public byte[] callMethod(String methodName, byte[] requestData, Object context);
};
