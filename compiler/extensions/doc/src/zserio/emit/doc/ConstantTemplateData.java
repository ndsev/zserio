package zserio.emit.doc;

import zserio.ast.Constant;
import zserio.emit.common.ZserioEmitException;

public class ConstantTemplateData extends DocTemplateData
{
    public ConstantTemplateData(TemplateDataContext context, Constant constant)
            throws ZserioEmitException
    {
        super(context, constant, constant.getName(), new LinkedType(constant.getTypeInstantiation()));

        value = context.getExpressionFormatter().formatGetter(constant.getValueExpression());
    }

    public String getValue()
    {
        return value;
    }

    private final String value;
}