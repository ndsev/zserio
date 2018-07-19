package zserio.emit.cpp;

import zserio.ast.Subtype;
import zserio.emit.cpp.types.CppNativeType;
import zserio.emit.cpp.types.NativeSubType;

public class SubtypeEmitterTemplateData extends UserTypeTemplateData
{
    public SubtypeEmitterTemplateData(TemplateDataContext context, Subtype type)
    {
        super(context, type);

        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();
        final NativeSubType nativeType = cppNativeTypeMapper.getCppSubType(type);
        final CppNativeType targetNativeType = nativeType.getTargetType();

        addHeaderIncludesForType(targetNativeType);

        targetCppTypeName = targetNativeType.getFullName();
    }

    public String getTargetCppTypeName()
    {
        return targetCppTypeName;
    }

    private final String targetCppTypeName;
}
