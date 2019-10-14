package zserio.emit.cpp;

import zserio.ast.Subtype;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.CppNativeType;

public class SubtypeEmitterTemplateData extends UserTypeTemplateData
{
    public SubtypeEmitterTemplateData(TemplateDataContext context, Subtype subtype) throws ZserioEmitException
    {
        super(context, subtype);

        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();

        final CppNativeType targetNativeType =
                cppNativeTypeMapper.getCppType(subtype.getTypeReference());
        addHeaderIncludesForType(targetNativeType);

        targetCppTypeName = targetNativeType.getFullName();
    }

    public String getTargetCppTypeName()
    {
        return targetCppTypeName;
    }

    private final String targetCppTypeName;
}
