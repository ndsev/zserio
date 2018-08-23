package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;
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
        serviceTypeList.add((ServiceType)token);
    }

    public void endRoot() throws ZserioEmitCppException
    {
        if (!getWithGrpcCode())
            return;

        final TemplateDataContext templateDataContext = getTemplateDataContext();
        for (ServiceType serviceType : serviceTypeList)
        {
            final ServiceEmitterTemplateData templateData =
                    new ServiceEmitterTemplateData(templateDataContext, serviceType);
            processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, serviceType);
            processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, serviceType);
        }

        final GrpcSerializationTraitsTemplateData traitsTemplateData =
                new GrpcSerializationTraitsTemplateData(templateDataContext, serviceTypeList);
        processHeaderTemplateToRootDir(TRAITS_TEMPLATE_HEADER_NAME, traitsTemplateData,
                TRAITS_OUTPUT_FILE_NAME_ROOT);
    }

    private List<ServiceType> serviceTypeList = new ArrayList<ServiceType>();

    private static final String TEMPLATE_SOURCE_NAME = "Service.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Service.h.ftl";

    private static final String TRAITS_TEMPLATE_HEADER_NAME = "GrpcSerializationTraits.h.ftl";
    private static final String TRAITS_OUTPUT_FILE_NAME_ROOT = "GrpcSerializationTraits";
}
