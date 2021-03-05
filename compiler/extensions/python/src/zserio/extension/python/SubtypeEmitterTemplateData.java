package zserio.extension.python;

import zserio.ast.Subtype;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

public class SubtypeEmitterTemplateData extends PythonTemplateData
{
    public SubtypeEmitterTemplateData(TemplateDataContext context, Subtype subtype)
            throws ZserioExtensionException
    {
        super(context);

        name = subtype.getName();

        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        final PythonNativeType nativeTargetType =
                pythonNativeMapper.getPythonType(subtype.getTypeReference());
        importType(nativeTargetType);
        targetTypeName = nativeTargetType.getFullName();
    }

    public String getName()
    {
        return name;
    }

    public String getTargetTypeName()
    {
        return targetTypeName;
    }

    private final String name;
    private final String targetTypeName;
}
