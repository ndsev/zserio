package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Rpc;
import zserio.ast.ServiceType;
import zserio.ast.ZserioType;
import zserio.emit.java.JavaNativeTypeMapper;

public final class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
    {
        super(context, serviceType);

        className = serviceType.getName() + "Grpc";

        final JavaNativeTypeMapper javaTypeMapper = context.getJavaNativeTypeMapper();
        Iterable<Rpc> rpcList = serviceType.getRpcList();
        for (Rpc rpc : rpcList)
        {
            this.rpcList.add(new RpcTemplateData(javaTypeMapper, rpc));
        }
    }

    public Iterable<RpcTemplateData> getRpcList()
    {
        return rpcList;
    }

    public String getClassName()
    {
        return className;
    }

    public static class RpcTemplateData
    {
        public RpcTemplateData(JavaNativeTypeMapper typeMapper, Rpc rpc)
        {
            name = rpc.getName();

            final ZserioType responseType = rpc.getResponseType();
            responseTypeFullName = typeMapper.getJavaType(responseType).getFullName();

            final ZserioType requestType = rpc.getRequestType();
            requestTypeFullName = typeMapper.getJavaType(requestType).getFullName();
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

        private final String name;
        private final String responseTypeFullName;
        private final String requestTypeFullName;
    }

    private final String className;
    private final List<RpcTemplateData> rpcList = new ArrayList<RpcTemplateData>();
}
