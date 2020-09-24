package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.ChoiceType;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.HashUtil;

public class BitmaskTemplateData extends DocTemplateData
{
    public BitmaskTemplateData(TemplateDataContext context, BitmaskType bitmaskType)
            throws ZserioEmitException
    {
        super(context, bitmaskType, bitmaskType.getName());

        linkedType = new LinkedType(bitmaskType.getTypeInstantiation());

        for (BitmaskValue value : bitmaskType.getValues())
        {
            values.add(new BitmaskValueTemplateData(value, context.getUsedByCollector(),
                    context.getExpressionFormatter()));
        }
    }

    public LinkedType getLinkedType()
    {
        return linkedType;
    }

    public Iterable<BitmaskValueTemplateData> getValues()
    {
        return values;
    }

    public static class BitmaskValueTemplateData
    {
        public BitmaskValueTemplateData(BitmaskValue bitmaskValue, UsedByCollector usedByCollector,
                ExpressionFormatter docExpressionFormatter) throws ZserioEmitException
        {
            name = bitmaskValue.getName();

            final Expression valueExpression = bitmaskValue.getValueExpression();
            value = (valueExpression == null) ? bitmaskValue.getValue().toString() :
                docExpressionFormatter.formatGetter(bitmaskValue.getValueExpression());

            docComment = new DocCommentTemplateData(bitmaskValue.getDocComment());

            usageInfoList = new TreeSet<UsageInfoTemplateData>();
            for (ChoiceType choiceType : usedByCollector.getUsedByChoices(bitmaskValue))
                usageInfoList.add(new UsageInfoTemplateData(bitmaskValue, choiceType));
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
            private final BitmaskValue bitmaskValue;
            private final ChoiceType choiceType;

            public UsageInfoTemplateData(BitmaskValue bitmaskValue, ChoiceType choiceType)
            {
                this.bitmaskValue = bitmaskValue;
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

            public BitmaskValue getBitmaskValue()
            {
                return bitmaskValue;
            }

            public boolean getIsFromChoiceCase()
            {
                return choiceType != null;
            }

            public String getChoiceCaseLinkText()
            {
                return (choiceType.getName() + "( " + getBitmaskValue().getName() + " )");
            }

            public String getChoiceCaseLink() throws ZserioEmitException
            {
                return DocEmitterTools.getUrlNameFromType(choiceType) +
                        "#casedef_" + getBitmaskValue().getName();
            }
        };

        private final String name;
        private final String value;
        private final DocCommentTemplateData docComment;
        private final SortedSet<UsageInfoTemplateData> usageInfoList;
    }

    private final LinkedType linkedType;
    private final List<BitmaskValueTemplateData> values = new ArrayList<BitmaskValueTemplateData>();
}
