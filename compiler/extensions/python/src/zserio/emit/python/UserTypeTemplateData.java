package zserio.emit.python;

import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public class UserTypeTemplateData extends PythonTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioEmitException
    {
        nativeType = context.getPythonNativeTypeMapper().getPythonType(type);
        name = nativeType.getName();
    }

    public String getName()
    {
        return name;
    }

    protected PythonNativeType getNativeType()
    {
        return nativeType;
    }

    private final PythonNativeType nativeType;
    private final String name;
}
