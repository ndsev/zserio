package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Rpc;
import zserio.ast.ServiceType;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.JavaNativeMapper;

public final class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioEmitException
    {
        super(context, serviceType);

        className = serviceType.getName() + "Grpc";

        final JavaNativeMapper javaTypeMapper = context.getJavaNativeMapper();
        final Iterable<Rpc> rpcList = serviceType.getRpcList();
        for (Rpc rpc : rpcList)
        {
            RpcTemplateData templateData = new RpcTemplateData(javaTypeMapper, rpc);
            this.rpcList.add(templateData);
            if (templateData.getNoStreaming())
                hasNoStreamingRpc = true;
            else if (templateData.getRequestOnlyStreaming())
                hasRequestOnlyStreamingRpc = true;
            else if (templateData.getResponseOnlyStreaming())
                hasResponseOnlyStreamingRpc = true;
            else if (templateData.getBidiStreaming())
                hasBidiStreamingRpc = true;
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

    public boolean getHasNoStreamingRpc()
    {
        return hasNoStreamingRpc;
    }

    public boolean getHasRequestOnlyStreamingRpc()
    {
        return hasRequestOnlyStreamingRpc;
    }

    public boolean getHasResponseOnlyStreamingRpc()
    {
        return hasResponseOnlyStreamingRpc;
    }

    public boolean getHasBidiStreamingRpc()
    {
        return hasBidiStreamingRpc;
    }

    public static class RpcTemplateData
    {
        public RpcTemplateData(JavaNativeMapper typeMapper, Rpc rpc) throws ZserioEmitException
        {
            name = rpc.getName();

            final ZserioType responseType = rpc.getResponseType();
            responseTypeFullName = typeMapper.getJavaType(responseType).getFullName();
            hasResponseStreaming = rpc.hasResponseStreaming();

            final ZserioType requestType = rpc.getRequestType();
            requestTypeFullName = typeMapper.getJavaType(requestType).getFullName();
            hasRequestStreaming = rpc.hasRequestStreaming();
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

        public boolean getNoStreaming()
        {
            return !hasRequestStreaming && !hasResponseStreaming;
        }

        public boolean getRequestOnlyStreaming()
        {
            return hasRequestStreaming && !hasResponseStreaming;
        }

        public boolean getResponseOnlyStreaming()
        {
            return !hasRequestStreaming && hasResponseStreaming;
        }

        public boolean getBidiStreaming()
        {
            return hasRequestStreaming && hasResponseStreaming;
        }

        private final String name;
        private final String responseTypeFullName;
        private final boolean hasResponseStreaming;
        private final String requestTypeFullName;
        private final boolean hasRequestStreaming;
    }

    private final String className;
    private final List<RpcTemplateData> rpcList = new ArrayList<RpcTemplateData>();
    private boolean hasNoStreamingRpc;
    private boolean hasRequestOnlyStreamingRpc;
    private boolean hasResponseOnlyStreamingRpc;
    private boolean hasBidiStreamingRpc;
}
