package zserio.emit.doc;

import zserio.ast.InstantiateType;
import zserio.emit.common.ZserioEmitException;

public class InstantiateTypeTemplateData extends HtmlTemplateData
{
    public InstantiateTypeTemplateData(TemplateDataContext context, InstantiateType instantiateType)
            throws ZserioEmitException
    {
        super(context, instantiateType);

        typeSymbol = SymbolTemplateDataCreator.createData(context, instantiateType.getTypeReference());
    }

    public SymbolTemplateData getTypeSymbol()
    {
        return typeSymbol;
    }

    private final SymbolTemplateData typeSymbol;
};
