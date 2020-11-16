package zserio.extension.doc;

import zserio.ast.Subtype;
import zserio.extension.common.ZserioExtensionException;

public class SubtypeTemplateData extends HtmlTemplateData
{
    public SubtypeTemplateData(PackageTemplateDataContext context, Subtype subtype) throws ZserioExtensionException
    {
        super(context, subtype);

        typeSymbol = SymbolTemplateDataCreator.createData(context, subtype.getTypeReference());
    }

    public SymbolTemplateData getTypeSymbol()
    {
        return typeSymbol;
    }

    private final SymbolTemplateData typeSymbol;
};
