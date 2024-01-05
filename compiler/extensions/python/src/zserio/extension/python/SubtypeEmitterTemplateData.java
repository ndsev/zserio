package zserio.extension.python;

import zserio.ast.Subtype;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data for SubtypeEmitter.
 */
public final class SubtypeEmitterTemplateData extends UserTypeTemplateData
{
    public SubtypeEmitterTemplateData(TemplateDataContext context, Subtype subtype)
            throws ZserioExtensionException
    {
        super(context, subtype, subtype);

        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        final PythonNativeType nativeTargetType = pythonNativeMapper.getPythonType(subtype.getTypeReference());
        importType(nativeTargetType);
        targetTypeName = PythonFullNameFormatter.getFullName(nativeTargetType);
    }

    public String getTargetTypeName()
    {
        return targetTypeName;
    }

    private final String targetTypeName;
}
