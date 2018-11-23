package zserio.emit.python;

import zserio.ast.Subtype;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public class SubtypeEmitterTemplateData extends PythonTemplateData
{
    public SubtypeEmitterTemplateData(TemplateDataContext context, Subtype subtype)
            throws ZserioEmitException
    {
        name = subtype.getName();

        final PythonNativeTypeMapper pythonNativeTypeMapper = context.getPythonNativeTypeMapper();
        final PythonNativeType nativeTargetType = pythonNativeTypeMapper.getPythonType(subtype.getTargetType());
        targetTypeName = nativeTargetType.getFullName();

        importType(nativeTargetType);
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
