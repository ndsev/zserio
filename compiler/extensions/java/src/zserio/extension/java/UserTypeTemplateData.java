package zserio.extension.java;

import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * Base class for all user type template data for FreeMarker.
 */
public class UserTypeTemplateData extends JavaTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioExtensionException
    {
        super(context);

        final JavaNativeType javaNativeType = context.getJavaNativeMapper().getJavaType(type);
        packageName = JavaFullNameFormatter.getFullName(javaNativeType.getPackageName());
        name = javaNativeType.getName();
        schemaTypeName = ZserioTypeUtil.getFullName(type);
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getName()
    {
        return name;
    }

    public String getSchemaTypeName()
    {
        return schemaTypeName;
    }

    private final String packageName;
    private final String name;
    private final String schemaTypeName;
}
