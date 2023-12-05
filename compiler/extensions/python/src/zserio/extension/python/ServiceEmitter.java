package zserio.extension.python;

import zserio.ast.ServiceType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * Service emitter.
 */
final class ServiceEmitter extends PythonDefaultEmitter
{
    public ServiceEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        super(outputFileManager, pythonParameters);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioExtensionException
    {
        if (!getWithServiceCode())
            return;

        final ServiceEmitterTemplateData templateData =
                new ServiceEmitterTemplateData(getTemplateDataContext(), serviceType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, serviceType);
    }

    static final String TEMPLATE_SOURCE_NAME = "Service.py.ftl";
}
