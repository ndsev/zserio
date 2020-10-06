package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class EnumerationTemplateData extends DocTemplateData
{
    public EnumerationTemplateData(TemplateDataContext context, EnumType enumType)
            throws ZserioEmitException
    {
        super(context, enumType, enumType.getName());

        typeSymbol = SymbolTemplateDataCreator.createData(context, enumType.getTypeInstantiation());

        items = new ArrayList<EnumItemTemplateData>();
        for (EnumItem item: enumType.getItems())
            items.add(new EnumItemTemplateData(context, enumType, item));
    }

    public SymbolTemplateData getTypeSymbol()
    {
        return typeSymbol;
    }

    public Iterable<EnumItemTemplateData> getItems()
    {
        return items;
    }

    public static class EnumItemTemplateData
    {
        public EnumItemTemplateData(TemplateDataContext context, EnumType enumType, EnumItem enumItem)
                throws ZserioEmitException
        {
            symbol = SymbolTemplateDataCreator.createData(context, enumType, enumItem);

            final Expression valueExpression = enumItem.getValueExpression();
            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
            value = (valueExpression == null) ? enumItem.getValue().toString() :
                docExpressionFormatter.formatGetter(valueExpression);

            docComments = new DocCommentsTemplateData(context, enumItem.getDocComments());

            seeSymbols = new ArrayList<SymbolTemplateData>();
            final UsedByCollector usedByCollector = context.getUsedByCollector();
            for (UsedByCollector.ChoiceCaseReference choiceCaseRef : usedByCollector.getUsedByChoices(enumItem))
            {
                seeSymbols.add(SymbolTemplateDataCreator.createDataWithTypeName(context,
                        choiceCaseRef.getChoiceType(), choiceCaseRef.getChoiceCase(), enumItem.getName()));
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
    private final List<EnumItemTemplateData> items;
}
