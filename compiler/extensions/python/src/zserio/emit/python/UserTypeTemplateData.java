package zserio.emit.python;

import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public class UserTypeTemplateData extends PythonTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioEmitException
    {
        super(context);

        final PythonNativeType nativeType = context.getPythonNativeTypeMapper().getPythonType(type);
        name = nativeType.getName();
    }

    public String getName()
    {
        return name;
    }

    private final String name;
}
