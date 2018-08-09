package zserio.emit.cpp;

import zserio.ast.RpcType;
import zserio.ast.ZserioType;
import zserio.emit.cpp.types.CppNativeType;

public class RpcEmitterTemplateData
{
    public RpcEmitterTemplateData(CppNativeTypeMapper typeMapper, RpcType type)
    {
        rpcType = type;
        final CppNativeTypeMapper cppNativeTypeMapper = typeMapper;

        final ZserioType requestType = type.getRequestType();
        requestTypeFullName = cppNativeTypeMapper.getCppType(requestType).getFullName();
        final ZserioType responseType = type.getResponseType();
        responseTypeFullName = cppNativeTypeMapper.getCppType(responseType).getFullName();
    }

    public String getRequestTypeFullName()
    {
        return requestTypeFullName;
    }

    public String getResponseTypeFullName()
    {
        return responseTypeFullName;
    }

    public String getName()
    {
        return rpcType.getName();
    }

    final private RpcType rpcType;
    final private String requestTypeFullName;
    final private String responseTypeFullName;
}
