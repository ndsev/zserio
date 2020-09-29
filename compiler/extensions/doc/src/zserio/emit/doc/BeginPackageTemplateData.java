package zserio.emit.doc;

import zserio.ast.Package;
import zserio.emit.common.ZserioEmitException;

public class BeginPackageTemplateData
{
    public BeginPackageTemplateData(Package pkg) throws ZserioEmitException
    {
        name = pkg.getPackageName().toString();
        docComments = new DocCommentsTemplateData(pkg.getDocComments());
    }

    public String getName()
    {
        return name;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    private final String name;
    private final DocCommentsTemplateData docComments;
}
