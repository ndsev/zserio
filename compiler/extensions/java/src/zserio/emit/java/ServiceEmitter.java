package zserio.emit.java;

import zserio.ast.ServiceType;
import zserio.tools.Parameters;

class ServiceEmitter extends JavaDefaultEmitter
{
    public ServiceEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitJavaException
    {
        if (getWithGrpcCode())
        {
            final ServiceEmitterTemplateData templateData = new ServiceEmitterTemplateData(
                    getTemplateDataContext(), serviceType);
            processTemplate(TEMPLATE_NAME, templateData, serviceType, templateData.getClassName());
        }
    }

    private static final String TEMPLATE_NAME = "Service.java.ftl";
}
