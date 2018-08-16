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
            this.rpcList.add(new RpcTemplateData(cppTypeMapper, rpc));
        }
    }

    public Iterable<RpcTemplateData> getRpcList()
    {
        return rpcList;
    }

    public static class RpcTemplateData
    {
        public RpcTemplateData(CppNativeTypeMapper typeMapper, Rpc rpc)
        {
            final CppNativeTypeMapper cppNativeTypeMapper = typeMapper;

            name = rpc.getName();

            final ZserioType responseType = rpc.getResponseType();
            responseTypeFullName = cppNativeTypeMapper.getCppType(responseType).getFullName();

            final ZserioType requestType = rpc.getRequestType();
            requestTypeFullName = cppNativeTypeMapper.getCppType(requestType).getFullName();
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

        final private String name;
        final private String responseTypeFullName;
        final private String requestTypeFullName;
    }

    private final List<RpcTemplateData> rpcList = new ArrayList<RpcTemplateData>();
}
