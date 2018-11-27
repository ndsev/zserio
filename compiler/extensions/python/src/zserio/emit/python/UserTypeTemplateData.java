package zserio.emit.python;

import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public class UserTypeTemplateData extends PythonTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioEmitException
    {
        final PythonNativeType nativeType = context.getPythonNativeTypeMapper().getPythonType(type);
        name = nativeType.getName();
        withWriterCode = context.getWithWriterCode();
    }

    public String getName()
    {
        return name;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    private final String name;
    private final boolean withWriterCode;
}
