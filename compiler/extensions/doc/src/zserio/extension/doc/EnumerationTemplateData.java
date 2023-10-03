package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for enumerations in the package used by Package emitter.
 */
public class EnumerationTemplateData extends PackageTemplateDataBase
{
    public EnumerationTemplateData(PackageTemplateDataContext context, EnumType enumType)
            throws ZserioExtensionException
    {
        super(context, enumType);

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
        public EnumItemTemplateData(PackageTemplateDataContext context, EnumType enumType, EnumItem enumItem)
                throws ZserioExtensionException
        {
            symbol = SymbolTemplateDataCreator.createData(context, enumType, enumItem);

            final Expression valueExpression = enumItem.getValueExpression();
            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
            hasValueExpression = (valueExpression != null);
            value = hasValueExpression ? docExpressionFormatter.formatGetter(valueExpression) :
                    enumItem.getValue().toString();

            isDeprecated = enumItem.isDeprecated();
            isRemoved = enumItem.isRemoved();

            docComments = new DocCommentsTemplateData(context, enumItem.getDocComments());

            seeSymbols = new ArrayList<SeeSymbolTemplateData>();
            final UsedByChoiceCollector usedByChoiceCollector = context.getUsedByChoiceCollector();
            for (UsedByChoiceCollector.ChoiceCaseReference choiceCaseRef :
                usedByChoiceCollector.getUsedByChoices(enumItem))
            {
                final ChoiceType choiceType = choiceCaseRef.getChoiceType();
                final ChoiceCase choiceCase = choiceCaseRef.getChoiceCase();
                final Expression caseExpression = choiceCaseRef.getChoiceCaseExpression().getExpression();
                final SymbolTemplateData choiceCaseSymbol = SymbolTemplateDataCreator.createData(context,
                        choiceType, choiceCase, docExpressionFormatter.formatGetter(caseExpression));
                final SymbolTemplateData choiceTypeSymbol = SymbolTemplateDataCreator.createData(context,
                        choiceType);
                seeSymbols.add(new SeeSymbolTemplateData(choiceCaseSymbol, choiceTypeSymbol));
            }
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public boolean getHasValueExpression()
        {
            return hasValueExpression;
        }

        public String getValue()
        {
            return value;
        }

        public boolean getIsDeprecated()
        {
            return isDeprecated;
        }

        public boolean getIsRemoved()
        {
            return isRemoved;
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
        private final boolean hasValueExpression;
        private final String value;
        private final boolean isDeprecated;
        private final boolean isRemoved;
        private final DocCommentsTemplateData docComments;
        private final List<SeeSymbolTemplateData> seeSymbols;
    }

    private final SymbolTemplateData typeSymbol;
    private final List<EnumItemTemplateData> items;
}
