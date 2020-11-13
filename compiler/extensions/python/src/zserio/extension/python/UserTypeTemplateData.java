package zserio.extension.python;

import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

public class UserTypeTemplateData extends PythonTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioExtensionException
    {
        super(context);

        final PythonNativeType nativeType = context.getPythonNativeMapper().getPythonType(type);
        name = nativeType.getName();
    }

    public String getName()
    {
        return name;
    }

    private final String name;
}
