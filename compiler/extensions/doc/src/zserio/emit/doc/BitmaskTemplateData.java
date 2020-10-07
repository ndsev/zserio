package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceType;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class BitmaskTemplateData extends DocTemplateData
{
    public BitmaskTemplateData(TemplateDataContext context, BitmaskType bitmaskType) throws ZserioEmitException
    {
        super(context, bitmaskType);

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
            seeSymbols = new ArrayList<SeeSymbolTemplateData>();
            for (UsedByCollector.ChoiceCaseReference choiceCaseRef :
                usedByCollector.getUsedByChoices(bitmaskValue))
            {
                final ChoiceType choiceType = choiceCaseRef.getChoiceType();
                final ChoiceCase choiceCase = choiceCaseRef.getChoiceCase();
                final SymbolTemplateData choiceCaseSymbol = SymbolTemplateDataCreator.createData(context,
                        choiceType, choiceCase, bitmaskValue.getName());
                final SymbolTemplateData choiceTypeSymbol = SymbolTemplateDataCreator.createData(context,
                        choiceType);
                seeSymbols.add(new SeeSymbolTemplateData(choiceCaseSymbol, choiceTypeSymbol));
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

        public Iterable<SeeSymbolTemplateData> getSeeSymbols()
        {
            return seeSymbols;
        }

        private final SymbolTemplateData symbol;
        private final String value;
        private final DocCommentsTemplateData docComments;
        private final List<SeeSymbolTemplateData> seeSymbols;
    }

    private final SymbolTemplateData typeSymbol;
    private final List<BitmaskValueTemplateData> values;
}
