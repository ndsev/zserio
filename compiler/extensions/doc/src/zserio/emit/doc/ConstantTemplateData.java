package zserio.emit.doc;

import zserio.ast.Constant;
import zserio.emit.common.ZserioEmitException;

public class ConstantTemplateData extends DocTemplateData
{
    public ConstantTemplateData(TemplateDataContext context, Constant constant)
            throws ZserioEmitException
    {
        super(context, constant, constant.getName());

        linkedType = new LinkedType(constant.getTypeInstantiation());

        value = context.getExpressionFormatter().formatGetter(constant.getValueExpression());
    }

    public LinkedType getLinkedType()
    {
        return linkedType;
    }

    public String getValue()
    {
        return value;
    }

    private final LinkedType linkedType;
    private final String value;
}
