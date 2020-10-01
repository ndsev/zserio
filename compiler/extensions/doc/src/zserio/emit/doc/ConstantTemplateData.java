package zserio.emit.doc;

import zserio.ast.Constant;
import zserio.emit.common.ZserioEmitException;

public class ConstantTemplateData extends DocTemplateData
{
    public ConstantTemplateData(TemplateDataContext context, Constant constant)
            throws ZserioEmitException
    {
        super(context, constant, constant.getName());

        symbol = context.getSymbolTemplateDataMapper().getSymbol(constant.getTypeInstantiation());
        value = context.getExpressionFormatter().formatGetter(constant.getValueExpression());
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    public String getValue()
    {
        return value;
    }

    private final SymbolTemplateData symbol;
    private final String value;
}
