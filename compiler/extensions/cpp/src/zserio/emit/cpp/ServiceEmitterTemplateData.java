package zserio.emit.cpp;

import java.util.List;
import java.util.ArrayList;
import zserio.ast.ServiceType;
import zserio.ast.Rpc;
import zserio.ast.ZserioType;

public class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
    {
        super(context, serviceType);

        final CppNativeTypeMapper cppTypeMapper = context.getCppNativeTypeMapper();
        Iterable<Rpc> rpcList = serviceType.getRpcList();
        for (Rpc rpc : rpcList)
        {
            addHeaderIncludesForType(cppTypeMapper.getCppType(rpc.getResponseType()));
            addHeaderIncludesForType(cppTypeMapper.getCppType(rpc.getRequestType()));
            RpcTemplateData templateData = new RpcTemplateData(cppTypeMapper, rpc);
            this.rpcList.add(templateData);

            if (templateData.getNoStreaming())
            {
                noStreamingRpcList.add(templateData);
                noOrResponseOnlyStreamingRpcList.add(templateData);
            }
            else if (templateData.getResponseOnlyStreaming())
            {
                responseOnlyStreamingRpcList.add(templateData);
                noOrResponseOnlyStreamingRpcList.add(templateData);
            }
        }
    }

    public Iterable<RpcTemplateData> getRpcList()
    {
        return rpcList;
    }

    public Iterable<RpcTemplateData> getNoStreamingRpcList()
    {
        return noStreamingRpcList;
    }

    public Iterable<RpcTemplateData> getResponseOnlyStreamingRpcList()
    {
        return responseOnlyStreamingRpcList;
    }

    public Iterable<RpcTemplateData> getNoOrResponseOnlyStreamingRpcList()
    {
        return noOrResponseOnlyStreamingRpcList;
    }

    public static class RpcTemplateData
    {
        public RpcTemplateData(CppNativeTypeMapper typeMapper, Rpc rpc)
        {
            name = rpc.getName();

            final ZserioType responseType = rpc.getResponseType();
            responseTypeFullName = typeMapper.getCppType(responseType).getFullName();
            hasResponseStreaming = rpc.hasResponseStreaming();

            final ZserioType requestType = rpc.getRequestType();
            requestTypeFullName = typeMapper.getCppType(requestType).getFullName();
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

        final private String name;
        final private String responseTypeFullName;
        final private boolean hasResponseStreaming;
        final private String requestTypeFullName;
        final private boolean hasRequestStreaming;
    }

    private final List<RpcTemplateData> rpcList = new ArrayList<RpcTemplateData>();
    private final List<RpcTemplateData> noStreamingRpcList = new ArrayList<RpcTemplateData>();
    private final List<RpcTemplateData> responseOnlyStreamingRpcList = new ArrayList<RpcTemplateData>();
    private final List<RpcTemplateData> noOrResponseOnlyStreamingRpcList = new ArrayList<RpcTemplateData>();
}
