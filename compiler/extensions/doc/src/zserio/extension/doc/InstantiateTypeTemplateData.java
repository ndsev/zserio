package zserio.extension.doc;

import zserio.ast.InstantiateType;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for instantiate types in the package used by Package emitter.
 */
public class InstantiateTypeTemplateData extends PackageTemplateDataBase
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
