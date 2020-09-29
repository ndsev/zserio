package zserio.emit.doc;

import zserio.ast.Package;
import zserio.emit.common.ZserioEmitException;

public class EndPackageTemplateData
{
    public EndPackageTemplateData(Package pkg) throws ZserioEmitException
    {
        docComments = new DocCommentsTemplateData(pkg.getTrailingDocComments());
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    private final DocCommentsTemplateData docComments;
}
