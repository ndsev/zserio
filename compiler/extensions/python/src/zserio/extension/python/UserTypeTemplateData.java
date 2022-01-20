package zserio.extension.python;

import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * Base class for all user type template data for FreeMarker.
 */
public class UserTypeTemplateData extends PythonTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioExtensionException
    {
        super(context);

        final PythonNativeType nativeType = context.getPythonNativeMapper().getPythonType(type);
        name = nativeType.getName();
        schemaTypeFullName = ZserioTypeUtil.getFullName(type);
    }

    public String getName()
    {
        return name;
    }

    public String getSchemaTypeFullName()
    {
        return schemaTypeFullName;
    }

    private final String name;
    private final String schemaTypeFullName;
}
