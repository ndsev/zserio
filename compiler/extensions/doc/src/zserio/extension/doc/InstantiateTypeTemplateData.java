package zserio.extension.doc;

import zserio.ast.InstantiateType;
import zserio.extension.common.ZserioExtensionException;

public class InstantiateTypeTemplateData extends HtmlTemplateData
{
    public InstantiateTypeTemplateData(PackageTemplateDataContext context, InstantiateType instantiateType)
            throws ZserioExtensionException
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
