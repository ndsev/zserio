package zserio.emit.python;

import zserio.ast.ServiceType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class ServiceEmitter extends PythonDefaultEmitter
{
    public ServiceEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        if (getWithGrpcCode())
        {
            final ServiceEmitterTemplateData templateData = new ServiceEmitterTemplateData(
                    getTemplateDataContext(), serviceType);
            processSourceTemplate(TEMPLATE_NAME, templateData, serviceType);
        }
    }

    private static final String TEMPLATE_NAME = "Service.py.ftl";
}
