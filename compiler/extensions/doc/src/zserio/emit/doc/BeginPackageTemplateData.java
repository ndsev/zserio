package zserio.emit.doc;

import zserio.ast.Package;
import zserio.emit.common.ZserioEmitException;

public class BeginPackageTemplateData
{
    public BeginPackageTemplateData(Package pkg) throws ZserioEmitException
    {
        name = pkg.getPackageName().toString();
        docComment = new DocCommentTemplateData(pkg.getDocComment());
    }

    public String getName()
    {
        return name;
    }

    public DocCommentTemplateData getDocComment()
    {
        return docComment;
    }

    private final String name;
    private final DocCommentTemplateData docComment;
}