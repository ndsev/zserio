package zserio.extension.python;

import java.util.List;

import zserio.ast.DocComment;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * Base class for all user type template data for FreeMarker.
 */
public class UserTypeTemplateData extends PythonTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type, List<DocComment> docComments)
            throws ZserioExtensionException
    {
        super(context);

        final PythonNativeType nativeType = context.getPythonNativeMapper().getPythonType(type);
        name = nativeType.getName();
        schemaTypeFullName = ZserioTypeUtil.getFullName(type);
        this.docComments = docComments.isEmpty() ? null : new DocCommentsTemplateData(context, docComments);
    }

    public String getName()
    {
        return name;
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
    private final String schemaTypeFullName;
    private final DocCommentsTemplateData docComments;
}
