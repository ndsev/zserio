package zserio.extension.java;

import zserio.ast.ServiceType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * Service emitter.
 */
class ServiceEmitter extends JavaDefaultEmitter
{
    public ServiceEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters)
    {
        super(outputFileManager, javaParameters);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioExtensionException
    {
        if (!getWithServiceCode())
            return;

        final ServiceEmitterTemplateData templateData = new ServiceEmitterTemplateData(
                getTemplateDataContext(), serviceType);
        processTemplate(TEMPLATE_NAME, templateData, serviceType);
    }

    private static final String TEMPLATE_NAME = "Service.java.ftl";
}
