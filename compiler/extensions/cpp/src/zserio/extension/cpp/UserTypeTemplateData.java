package zserio.extension.cpp;

import zserio.ast.DocumentableAstNode;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

/**
 * Base class for all user type template data for FreeMarker.
 */
public class UserTypeTemplateData extends CppTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type,
            DocumentableAstNode documentableNode) throws ZserioExtensionException
    {
        super(context);

        nativeType = context.getCppNativeMapper().getCppType(type);
        schemaTypeName = ZserioTypeUtil.getFullName(type);
        packageData = new PackageTemplateData(nativeType);
        docComments = DocCommentsDataCreator.createData(context, documentableNode);
    }

    public String getName()
    {
        return nativeType.getName();
    }

    public String getFullName()
    {
        return nativeType.getFullName();
    }

    public String getSchemaTypeName()
    {
        return schemaTypeName;
    }

    public PackageTemplateData getPackage()
    {
        return packageData;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    private final CppNativeType nativeType;
    private final String schemaTypeName;
    private final PackageTemplateData packageData;
    private final DocCommentsTemplateData docComments;
}
