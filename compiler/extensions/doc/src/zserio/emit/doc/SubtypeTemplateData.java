package zserio.emit.doc;

import zserio.ast.Subtype;
import zserio.emit.common.ZserioEmitException;

public class SubtypeTemplateData extends DocTemplateData
{
    public SubtypeTemplateData(TemplateDataContext context, Subtype subtype) throws ZserioEmitException
    {
        super(context, subtype, subtype.getName());

        symbol = context.getSymbolTemplateDataMapper().getSymbol(subtype.getTypeReference());
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    private final SymbolTemplateData symbol;
};
