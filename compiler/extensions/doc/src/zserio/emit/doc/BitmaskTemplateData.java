package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class BitmaskTemplateData extends DocTemplateData
{
    public BitmaskTemplateData(TemplateDataContext context, BitmaskType bitmaskType)
            throws ZserioEmitException
    {
        super(context, bitmaskType, bitmaskType.getName());

        typeSymbol = SymbolTemplateDataCreator.createData(context, bitmaskType.getTypeInstantiation());

        values = new ArrayList<BitmaskValueTemplateData>();
        for (BitmaskValue value : bitmaskType.getValues())
        {
            values.add(new BitmaskValueTemplateData(context, bitmaskType, value));
        }
    }

    public SymbolTemplateData getTypeSymbol()
    {
        return typeSymbol;
    }

    public Iterable<BitmaskValueTemplateData> getValues()
    {
        return values;
    }

    public static class BitmaskValueTemplateData
    {
        public BitmaskValueTemplateData(TemplateDataContext context, BitmaskType bitmaskType,
                BitmaskValue bitmaskValue) throws ZserioEmitException
        {
            symbol = SymbolTemplateDataCreator.createData(context, bitmaskType, bitmaskValue);

            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
            final Expression valueExpression = bitmaskValue.getValueExpression();
            value = (valueExpression == null) ? bitmaskValue.getValue().toString() :
                docExpressionFormatter.formatGetter(bitmaskValue.getValueExpression());

            docComments = new DocCommentsTemplateData(context, bitmaskValue.getDocComments());

            final UsedByCollector usedByCollector = context.getUsedByCollector();
            seeSymbols = new ArrayList<SymbolTemplateData>();
            for (UsedByCollector.ChoiceCaseReference choiceCaseRef :
                usedByCollector.getUsedByChoices(bitmaskValue))
            {
                seeSymbols.add(SymbolTemplateDataCreator.createDataWithTypeName(context,
                        choiceCaseRef.getChoiceType(), choiceCaseRef.getChoiceCase(), bitmaskValue.getName()));
            }

        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public String getValue()
        {
            return value;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        public Iterable<SymbolTemplateData> getSeeSymbols()
        {
            return seeSymbols;
        }

        private final SymbolTemplateData symbol;
        private final String value;
        private final DocCommentsTemplateData docComments;
        private final List<SymbolTemplateData> seeSymbols;
    }

    private final SymbolTemplateData typeSymbol;
    private final List<BitmaskValueTemplateData> values;
}
