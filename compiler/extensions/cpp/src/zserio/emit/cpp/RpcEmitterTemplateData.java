package zserio.emit.cpp;

import zserio.ast.RpcType;
import zserio.ast.ZserioType;
import zserio.emit.cpp.types.CppNativeType;

public class RpcEmitterTemplateData extends CompoundTypeTemplateData
{
    public RpcEmitterTemplateData(TemplateDataContext context, RpcType type)
    {
        super(context, type);

	rpcType = type;
	requestType = type.getRequestType();
	responseType = type.getResponseType();

	final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();

	final CppNativeType nativeRequestType = cppNativeTypeMapper.getCppType(requestType);
	requestTypeFullName = nativeRequestType.getFullName();
	addHeaderIncludesForType(nativeRequestType);

	final CppNativeType nativeResponseType = cppNativeTypeMapper.getCppType(responseType);
	responseTypeFullName = nativeResponseType.getFullName();
	addHeaderIncludesForType(nativeResponseType);
    }

    public String getRequestTypeFullName()
    {
        return requestTypeFullName;
    }

    public String getResponseTypeFullName()
    {
        return responseTypeFullName;
    }

    final private RpcType rpcType;
    final private ZserioType requestType;
    final private ZserioType responseType;
    final private String requestTypeFullName;
    final private String responseTypeFullName;
}
