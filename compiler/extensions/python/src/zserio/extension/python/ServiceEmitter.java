package zserio.extension.python;

import zserio.ast.ServiceType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class ServiceEmitter extends PythonDefaultEmitter
{
    public ServiceEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioExtensionException
    {
        if (!getWithServiceCode())
            return;

        final ServiceEmitterTemplateData templateData = new ServiceEmitterTemplateData(
                getTemplateDataContext(), serviceType);
        processSourceTemplate(TEMPLATE_NAME, templateData, serviceType);
    }

    private static final String TEMPLATE_NAME = "Service.py.ftl";
}
