package zserio.extension.doc;

import zserio.ast.Package;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for end of the package used by Package emitter.
 */
public final class EndPackageTemplateData
{
    public EndPackageTemplateData(PackageTemplateDataContext context, Package pkg)
            throws ZserioExtensionException
    {
        jsDirectory = context.getJsDirectory();
        docComments = new DocCommentsTemplateData(context, pkg.getTrailingDocComments());
    }

    public String getJsDirectory()
    {
        return jsDirectory;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    private final String jsDirectory;
    private final DocCommentsTemplateData docComments;
}
