package zserio.extension.doc;

/**
 * FreeMarker template data for see symbol used by Package emitter.
 *
 * See symbol is used as an reference for example in description of the enumeration item which is used by some
 * choice case.
 */
public final class SeeSymbolTemplateData
{
    public SeeSymbolTemplateData(SymbolTemplateData memberSymbol, SymbolTemplateData typeSymbol)
    {
        this.memberSymbol = memberSymbol;
        this.typeSymbol = typeSymbol;
    }

    public SymbolTemplateData getMemberSymbol()
    {
        return memberSymbol;
    }

    public SymbolTemplateData getTypeSymbol()
    {
        return typeSymbol;
    }

    private final SymbolTemplateData memberSymbol;
    private final SymbolTemplateData typeSymbol;
}
