package zserio.emit.doc;

import zserio.ast.Subtype;
import zserio.emit.common.ZserioEmitException;

public class SubtypeTemplateData extends DocTemplateData
{
    public SubtypeTemplateData(TemplateDataContext context, Subtype subtype) throws ZserioEmitException
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
