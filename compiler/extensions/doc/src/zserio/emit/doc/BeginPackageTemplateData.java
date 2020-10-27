package zserio.emit.doc;

import zserio.ast.Package;
import zserio.emit.common.ZserioEmitException;

public class BeginPackageTemplateData
{
    public BeginPackageTemplateData(TemplateDataContext context, Package pkg) throws ZserioEmitException
    {
        name = AstNodeNameMapper.getName(pkg);
        packageName = AstNodePackageNameMapper.getPackageName(pkg).toString();
        docComments = new DocCommentsTemplateData(context, pkg.getDocComments());
    }

    public String getName()
    {
        return name;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    private final String name;
    private final String packageName;
    private final DocCommentsTemplateData docComments;
}
