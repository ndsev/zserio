package zserio.emit.doc;

public class SeeSymbolTemplateData
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
