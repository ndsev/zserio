package zserio.emit.cpp98;

import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.Rpc;
import zserio.ast.ServiceType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp98.types.CppNativeType;

public class GrpcSerializationTraitsTemplateData extends CppTemplateData
{
    public GrpcSerializationTraitsTemplateData(TemplateDataContext context, Iterable<ServiceType> serviceTypes)
            throws ZserioEmitException
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

    private final SortedSet<String> rpcTypeNames = new TreeSet<String>();
}