package zserio.extension.cpp;

import zserio.ast.Subtype;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

public class SubtypeEmitterTemplateData extends UserTypeTemplateData
{
    public SubtypeEmitterTemplateData(TemplateDataContext context, Subtype subtype) throws ZserioExtensionException
    {
        super(context, subtype);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();

        final CppNativeType targetNativeType =
                cppNativeMapper.getCppType(subtype.getTypeReference());
        addHeaderIncludesForType(targetNativeType);

        targetCppTypeName = targetNativeType.getFullName();
    }

    public String getTargetCppTypeName()
    {
        return targetCppTypeName;
    }

    private final String targetCppTypeName;
}
