package zserio.extension.doc;

import zserio.ast.Subtype;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for subtypes in the package used by Package emitter.
 */
public final class SubtypeTemplateData extends PackageTemplateDataBase
{
    public SubtypeTemplateData(PackageTemplateDataContext context, Subtype subtype)
            throws ZserioExtensionException
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
