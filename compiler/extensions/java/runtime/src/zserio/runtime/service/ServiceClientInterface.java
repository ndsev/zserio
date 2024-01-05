package zserio.runtime.service;

import zserio.runtime.io.Writer;

/** Generic interface for all Zserio services on client side. */
public interface ServiceClientInterface
{
    /**
     * Calls method with the given name synchronously.
     *
     * @param methodName Name of the service method to call.
     * @param request Request service data to be passed to the method.
     * @param context Context specific for particular service.
     *
     * @return Response data.
     */
    public byte[] callMethod(String methodName, ServiceData<? extends Writer> request, Object context);
}
