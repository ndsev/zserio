package zserio.emit.java;

import zserio.ast.ServiceType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class ServiceEmitter extends JavaDefaultEmitter
{
    public ServiceEmitter(JavaExtensionParameters javaParameters, Parameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        if (!getWithServiceCode())
            return;

        final ServiceEmitterTemplateData templateData = new ServiceEmitterTemplateData(
                getTemplateDataContext(), serviceType);
        processTemplate(TEMPLATE_NAME, templateData, serviceType);
    }

    private static final String TEMPLATE_NAME = "Service.java.ftl";
}
