package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.ServiceType;
import zserio.tools.Parameters;

class ServiceEmitter extends JavaDefaultEmitter
{
    public ServiceEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginService(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof ServiceType))
            throw new ZserioEmitJavaException("Unexpected token type in beginService!");

        if (getWithGrpcCode())
        {
            final ServiceType serviceType = (ServiceType)token;
            final ServiceEmitterTemplateData templateData = new ServiceEmitterTemplateData(
                    getTemplateDataContext(), serviceType);
            processTemplate(TEMPLATE_NAME, templateData, serviceType, templateData.getClassName());
        }
    }

    private static final String TEMPLATE_NAME = "Service.java.ftl";
}
