package zserio.extension.java;

import zserio.ast.DocumentableAstNode;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * Base class for all user type template data for FreeMarker.
 */
public class UserTypeTemplateData extends JavaTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type,
            DocumentableAstNode documentableNode) throws ZserioExtensionException
    {
        super(context);

        final JavaNativeType javaNativeType = context.getJavaNativeMapper().getJavaType(type);
        packageName = JavaFullNameFormatter.getFullName(javaNativeType.getPackageName());
        name = javaNativeType.getName();
        schemaTypeName = ZserioTypeUtil.getFullName(type);
        docComments = DocCommentsDataCreator.createData(context, documentableNode);
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

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    private final String packageName;
    private final String name;
    private final String schemaTypeName;
    private final DocCommentsTemplateData docComments;
}
