package zserio.extension.doc;

import zserio.ast.Constant;
import zserio.extension.common.ZserioExtensionException;

public class ConstantTemplateData extends HtmlTemplateData
{
    public ConstantTemplateData(PackageTemplateDataContext context, Constant constant) throws ZserioExtensionException
    {
        super(context, constant);

        typeSymbol = SymbolTemplateDataCreator.createData(context, constant.getTypeInstantiation());
        value = context.getExpressionFormatter().formatGetter(constant.getValueExpression());
    }

    public SymbolTemplateData getTypeSymbol()
    {
        return typeSymbol;
    }

    public String getValue()
    {
        return value;
    }

    private final SymbolTemplateData typeSymbol;
    private final String value;
}
