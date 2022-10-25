package zserio.extension.python;

import zserio.ast.DocumentableAstNode;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * Base class for all user type template data for FreeMarker.
 */
public class UserTypeTemplateData extends PythonTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type,
            DocumentableAstNode documentableNode) throws ZserioExtensionException
    {
        super(context);

        final PythonNativeType nativeType = context.getPythonNativeMapper().getPythonType(type);
        name = nativeType.getName();
        fullName = PythonFullNameFormatter.getFullName(nativeType);
        schemaTypeFullName = ZserioTypeUtil.getFullName(type);
        docComments = DocCommentsDataCreator.createData(context, documentableNode);
    }

    public String getName()
    {
        return name;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getSchemaTypeFullName()
    {
        return schemaTypeFullName;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    private final String name;
    private final String fullName;
    private final String schemaTypeFullName;
    private final DocCommentsTemplateData docComments;
}
