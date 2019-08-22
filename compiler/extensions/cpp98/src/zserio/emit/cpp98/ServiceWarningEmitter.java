package zserio.emit.cpp98;

import zserio.ast.ServiceType;
import zserio.tools.Parameters;
import zserio.tools.ZserioToolPrinter;

public class ServiceWarningEmitter extends CppDefaultEmitter
{
    public ServiceWarningEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginService(ServiceType service)
    {
        if (getWithGrpcCode())
        {
            if (!mainWarningReported)
            {
                ZserioToolPrinter.printWarning("GRPC services are not supported by the legacy C++98 emitter!");
                mainWarningReported = true;
            }
            ZserioToolPrinter.printWarning(service,
                    "Ignoring GRPC service '" + service.getName() + "'!");
        }
    }

    private boolean mainWarningReported = false;
}
