package zserio.runtime.service;

import zserio.runtime.io.Writer;

/** Generic interface for all Zserio services on server side. */
public interface ServiceInterface
{
    /**
     * Calls method with the given name synchronously.
     *
     * @param methodName Name of the service method to call.
     * @param requestData Request data to be passed to the method.
     * @param context Context specific for particular service.
     *
     * @return Response service data.
     */
    public ServiceData<? extends Writer> callMethod(String methodName, byte[] requestData, Object context);
}
