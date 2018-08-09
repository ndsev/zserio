package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.ServiceType;
import zserio.tools.Parameters;

public class ServiceEmitter extends CppDefaultEmitter
{
    public ServiceEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginService(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof ServiceType))
            throw new ZserioEmitCppException("Unexpected token type in beginService!");
        serviceType = (ServiceType)token;
    }

    public void endRoot() throws ZserioEmitCppException
    {
        final TemplateDataContext templateDataContext = getTemplateDataContext();
        final Object templateData = new ServiceEmitterTemplateData(templateDataContext, serviceType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, serviceType);
        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, serviceType);
    }

    private ServiceType serviceType;
    private static final String TEMPLATE_SOURCE_NAME = "Service.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Service.h.ftl";
}
