package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.ChoiceType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.HashUtil;

public class EnumerationTemplateData extends DocTemplateData
{
    public EnumerationTemplateData(TemplateDataContext context, EnumType enumType)
            throws ZserioEmitException
    {
        super(context, enumType, enumType.getName());

        linkedType = new LinkedType(enumType.getTypeInstantiation());

        for (EnumItem item: enumType.getItems())
        {
            items.add(new EnumItemTemplateData(item, context.getUsedByCollector(),
                    context.getExpressionFormatter()));
        }
    }

    public LinkedType getLinkedType()
    {
        return linkedType;
    }

    public Iterable<EnumItemTemplateData> getItems()
    {
        return items;
    }

    public static class EnumItemTemplateData
    {
        public EnumItemTemplateData(EnumItem enumItem, UsedByCollector usedByCollector,
                ExpressionFormatter docExpressionFormatter) throws ZserioEmitException
        {
            name = enumItem.getName();

            final Expression valueExpression = enumItem.getValueExpression();
            value = (valueExpression == null) ? enumItem.getValue().toString() :
                docExpressionFormatter.formatGetter(valueExpression);

            docComment = new DocCommentTemplateData(enumItem.getDocComment());

            usageInfoList = new TreeSet<UsageInfoTemplateData>();
            for (ChoiceType choiceType : usedByCollector.getUsedByChoices(enumItem))
                usageInfoList.add(new UsageInfoTemplateData(enumItem, choiceType));
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        public DocCommentTemplateData getDocComment()
        {
            return docComment;
        }

        public Iterable<UsageInfoTemplateData> getUsageInfoList()
        {
            return usageInfoList;
        }

        public static class UsageInfoTemplateData implements Comparable<UsageInfoTemplateData>
        {
            private final EnumItem enumItem;
            private final ChoiceType choiceType;

            public UsageInfoTemplateData(EnumItem enumItem, ChoiceType choiceType)
            {
                this.enumItem = enumItem;
                this.choiceType = choiceType;
            }

            /* Don't change this ordering to have always the same generated HTML sources. */
            @Override
            public int compareTo(UsageInfoTemplateData other)
            {
                return getChoiceCaseLinkText().compareTo(other.getChoiceCaseLinkText());
            }

            @Override
            public boolean equals(Object other)
            {
                if ( !(other instanceof UsageInfoTemplateData) )
                    return false;

                return (this == other) || compareTo((UsageInfoTemplateData)other) == 0;
            }

            @Override
            public int hashCode()
            {
                int hash = HashUtil.HASH_SEED;
                hash = HashUtil.hash(hash, getChoiceCaseLinkText());
                return hash;
            }

            public EnumItem getEnumItem()
            {
                return enumItem;
            }

            public boolean getIsFromChoiceCase()
            {
                return choiceType != null;
            }

            public String getChoiceCaseLinkText()
            {
                return (choiceType.getName() + "( " + getEnumItem().getName() + " )");
            }

            public String getChoiceCaseLink() throws ZserioEmitException
            {
                return DocEmitterTools.getUrlNameFromType(choiceType) + "#casedef_" + getEnumItem().getName();
            }
        }

        private final String name;
        private final String value;
        private final DocCommentTemplateData docComment;
        private final SortedSet<UsageInfoTemplateData> usageInfoList;
    }

    private final LinkedType linkedType;
    private final List<EnumItemTemplateData> items = new ArrayList<EnumItemTemplateData>();
}
