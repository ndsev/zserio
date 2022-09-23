package zserio.extension.cpp;

import zserio.ast.Subtype;
import zserio.ast.TypeReference;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

/**
 * FreeMarker template data for SubtypeEmitter.
 */
public class SubtypeEmitterTemplateData extends UserTypeTemplateData
{
    public SubtypeEmitterTemplateData(TemplateDataContext context, Subtype subtype)
            throws ZserioExtensionException
    {
        super(context, subtype, subtype.getDocComments());

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();

        final TypeReference subtypeTypeReference = subtype.getTypeReference();
        final CppNativeType targetNativeType = cppNativeMapper.getCppType(subtypeTypeReference);
        addHeaderIncludesForType(targetNativeType);

        targetTypeInfo = new NativeTypeInfoTemplateData(targetNativeType, subtypeTypeReference);
    }

    public NativeTypeInfoTemplateData getTargetTypeInfo()
    {
        return targetTypeInfo;
    }

    private final NativeTypeInfoTemplateData targetTypeInfo;
}
