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

        withPythonProperties = context.getWithPythonProperties();
    }

    public String getName()
    {
        return name;
    }

    public boolean getWithPythonProperties()
    {
        return withPythonProperties;
    }

    private final String name;
    private final boolean withPythonProperties;
}
