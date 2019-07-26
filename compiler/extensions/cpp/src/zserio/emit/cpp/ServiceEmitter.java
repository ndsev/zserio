package zserio.emit.cpp;

import java.util.HashSet;
import java.util.Set;

import zserio.ast.ServiceType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.ServiceEmitterTemplateData.RpcTemplateData;
import zserio.tools.Parameters;

public class ServiceEmitter extends CppDefaultEmitter
{
    public ServiceEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        if (!getWithGrpcCode())
            return;

        final TemplateDataContext templateDataContext = getTemplateDataContext();
        final ServiceEmitterTemplateData templateData =
                new ServiceEmitterTemplateData(templateDataContext, serviceType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, serviceType);
        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, serviceType);

        addRpcTypes(templateData.getRpcList());
    }

    public Set<String> getRpcTypeNames()
    {
        return rpcTypeNames;
    }

    private void addRpcTypes(Iterable<RpcTemplateData> rpcList)
    {
        for (RpcTemplateData rpc : rpcList)
        {
            rpcTypeNames.add(rpc.getRequestTypeFullName());
            rpcTypeNames.add(rpc.getResponseTypeFullName());
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "Service.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Service.h.ftl";

    private final Set<String> rpcTypeNames = new HashSet<String>();
}
