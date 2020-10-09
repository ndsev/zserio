package zserio.emit.doc;

import zserio.ast.Constant;
import zserio.emit.common.ZserioEmitException;

public class ConstantTemplateData extends HtmlTemplateData
{
    public ConstantTemplateData(TemplateDataContext context, Constant constant) throws ZserioEmitException
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
