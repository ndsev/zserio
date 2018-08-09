package zserio.emit.cpp;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import zserio.ast.ServiceType;
import zserio.ast.RpcType;
import zserio.ast.ZserioType;
import zserio.emit.cpp.types.CppNativeType;

public class ServiceEmitterTemplateData extends CompoundTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType type)
    {
        super(context, type);

        serviceType = type;
        cppNativeTypeMapper = context.getCppNativeTypeMapper();
    }

    @Override
    public Iterable<String> getHeaderUserIncludes()
    {
        serviceType.getParameterTypes().forEach((p) -> {
            addHeaderIncludesForType(cppNativeTypeMapper.getCppType(p));
        });

        return super.getHeaderUserIncludes();
    }

    public Iterable<RpcEmitterTemplateData> getRpcList()
    {
        List<RpcEmitterTemplateData> rpcs = new ArrayList<RpcEmitterTemplateData>();

        serviceType.getRpcList().forEach((r) -> {
            rpcs.add(new RpcEmitterTemplateData(cppNativeTypeMapper, r));
        });

        return rpcs;
    }

    public Iterable<String> getParameterTypeNames()
    {
        Set<String> typeNames = new HashSet<String>();
        serviceType.getParameterTypes().forEach((p) -> {
            typeNames.add(
                cppNativeTypeMapper.getCppType(p).getFullName());
        });
        return typeNames;
    }

    public final ServiceType serviceType;
    private final CppNativeTypeMapper cppNativeTypeMapper;
}
