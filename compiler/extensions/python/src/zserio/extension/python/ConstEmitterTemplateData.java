package zserio.extension.python;

import zserio.ast.Constant;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.symbols.PythonNativeSymbol;

/**
 * FreeMarker template data for ConstEmitter.
 */
public final class ConstEmitterTemplateData extends PythonTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, Constant constant)
            throws ZserioExtensionException
    {
        super(context);

        final PythonNativeSymbol nativeSymbol = context.getPythonNativeMapper().getPythonSymbol(constant);
        name = nativeSymbol.getName();

        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        value = pythonExpressionFormatter.formatGetter(constant.getValueExpression());
        docComments = DocCommentsDataCreator.createData(context, constant);
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    private final String name;
    private final String value;
    private final DocCommentsTemplateData docComments;
}
