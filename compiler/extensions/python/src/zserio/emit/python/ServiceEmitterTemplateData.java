package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Rpc;
import zserio.ast.ServiceType;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public final class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioEmitException
    {
        super(context, serviceType);

        final PythonNativeTypeMapper pythonTypeMapper = context.getPythonNativeTypeMapper();

        final PythonNativeType nativeServiceType = pythonTypeMapper.getPythonType(serviceType);
        packageName = PythonFullNameFormatter.getFullName(nativeServiceType.getPackageName());

        final Iterable<Rpc> rpcList = serviceType.getRpcList();
        for (Rpc rpc : rpcList)
        {
            final RpcTemplateData templateData = new RpcTemplateData(pythonTypeMapper, rpc, this);
            this.rpcList.add(templateData);
        }

        importPackage("zserio");
        importPackage("grpc");
    }

    public String getPackageName()
    {
        return packageName;
    }

    public Iterable<RpcTemplateData> getRpcList()
    {
        return rpcList;
    }

    public static class RpcTemplateData
    {
        public RpcTemplateData(PythonNativeTypeMapper typeMapper, Rpc rpc, ImportCollector importCollector)
                throws ZserioEmitException
        {
            name = rpc.getName();

            final ZserioType responseType = rpc.getResponseType();
            final PythonNativeType pythonResponseType = typeMapper.getPythonType(responseType);
            importCollector.importType(pythonResponseType);
            responseTypeFullName = pythonResponseType.getFullName();
            responseStreaming = rpc.hasResponseStreaming() ? "stream" : "unary";

            final ZserioType requestType = rpc.getRequestType();
            final PythonNativeType pythonRequestType = typeMapper.getPythonType(requestType);
            importCollector.importType(pythonRequestType);
            requestTypeFullName = pythonRequestType.getFullName();
            requestStreaming = rpc.hasRequestStreaming() ? "stream" : "unary";
        }

        public String getName()
        {
            return name;
        }

        public String getResponseTypeFullName()
        {
            return responseTypeFullName;
        }

        public String getRequestTypeFullName()
        {
            return requestTypeFullName;
        }

        public String getRequestStreaming()
        {
            return requestStreaming;
        }

        public String getResponseStreaming()
        {
            return responseStreaming;
        }

        private final String name;
        private final String responseTypeFullName;
        private final String responseStreaming;
        private final String requestTypeFullName;
        private final String requestStreaming;
    }

    private final String packageName;
    private final List<RpcTemplateData> rpcList = new ArrayList<RpcTemplateData>();
}
