package zserio.emit.cpp;

import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.Rpc;
import zserio.ast.ServiceType;
import zserio.emit.cpp.types.CppNativeType;

public class GrpcSerializationTraitsTemplateData extends CppTemplateData
{
    public GrpcSerializationTraitsTemplateData(TemplateDataContext context, Iterable<ServiceType> serviceTypes)
    {
        super(context);

        CppNativeTypeMapper cppTypeMapper = context.getCppNativeTypeMapper();

        for (ServiceType serviceType : serviceTypes)
        {
            for (Rpc rpc : serviceType.getRpcList())
            {
                addRpcType(cppTypeMapper.getCppType(rpc.getResponseType()));
                addRpcType(cppTypeMapper.getCppType(rpc.getRequestType()));
            }
        }
    }

    public Iterable<String> getRpcTypeNames()
    {
        return rpcTypeNames;
    }

    private void addRpcType(CppNativeType rpcNativeType)
    {
        addHeaderIncludesForType(rpcNativeType);
        rpcTypeNames.add(rpcNativeType.getFullName());
    }

    private SortedSet<String> rpcTypeNames = new TreeSet<String>();
}