package zserio.emit.cpp;

import java.util.Set;
import java.util.TreeSet;

import zserio.ast.CompoundType;
import zserio.ast.Rpc;
import zserio.ast.ServiceType;
import zserio.emit.common.ZserioEmitException;
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

        addRpcTypes(serviceType.getRpcList());
    }

    public Set<CompoundType> getRpcTypes()
    {
        return rpcTypes;
    }

    private void addRpcTypes(Iterable<Rpc> rpcList)
    {
        for (Rpc rpc : rpcList)
        {
            rpcTypes.add(rpc.getRequestType());
            rpcTypes.add(rpc.getResponseType());
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "Service.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Service.h.ftl";

    private final Set<CompoundType> rpcTypes = new TreeSet<CompoundType>();
}
