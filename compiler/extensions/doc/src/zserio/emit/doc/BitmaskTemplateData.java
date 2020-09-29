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
import zserio.tools.StringJoinUtil;

public class BitmaskTemplateData extends DocTemplateData
{
    public BitmaskTemplateData(TemplateDataContext context, BitmaskType bitmaskType)
            throws ZserioEmitException
    {
        super(context, bitmaskType, bitmaskType.getName());

        linkedType = new LinkedType(bitmaskType.getTypeInstantiation());

        for (BitmaskValue value : bitmaskType.getValues())
        {
            values.add(new BitmaskValueTemplateData(bitmaskType, value, context.getUsedByCollector(),
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
        public BitmaskValueTemplateData(BitmaskType bitmaskType, BitmaskValue bitmaskValue,
                UsedByCollector usedByCollector,
                ExpressionFormatter docExpressionFormatter) throws ZserioEmitException
        {
            name = bitmaskValue.getName();
            anchorName = DocEmitterTools.getAnchorName(bitmaskType, name);

            final Expression valueExpression = bitmaskValue.getValueExpression();
            value = (valueExpression == null) ? bitmaskValue.getValue().toString() :
                docExpressionFormatter.formatGetter(bitmaskValue.getValueExpression());

            docComments = new DocCommentsTemplateData(bitmaskValue.getDocComments());

            usageInfoList = new TreeSet<UsageInfoTemplateData>();
            for (ChoiceType choiceType : usedByCollector.getUsedByChoices(bitmaskValue))
                usageInfoList.add(new UsageInfoTemplateData(bitmaskValue, choiceType));
        }

        public String getName()
        {
            return name;
        }

        public String getAnchorName()
        {
            return anchorName;
        }

        public String getValue()
        {
            return value;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        public Iterable<UsageInfoTemplateData> getUsageInfoList()
        {
            return usageInfoList;
        }

        public static class UsageInfoTemplateData implements Comparable<UsageInfoTemplateData>
        {
            public UsageInfoTemplateData(BitmaskValue bitmaskValue, ChoiceType choiceType)
                    throws ZserioEmitException
            {
                this.choiceCaseLinkText = choiceType.getName() + "( " + bitmaskValue.getName() + " )";
                final String urlName = DocEmitterTools.getUrlNameFromType(choiceType);
                final String anchorName = DocEmitterTools.getAnchorName(
                        choiceType, "casedef", bitmaskValue.getName());
                this.choiceCaseLink = StringJoinUtil.joinStrings(urlName, anchorName, "#");
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

            public String getChoiceCaseLinkText()
            {
                return choiceCaseLinkText;
            }

            public String getChoiceCaseLink()
            {
                return choiceCaseLink;
            }

            private final String choiceCaseLinkText;
            private final String choiceCaseLink;
        };

        private final String name;
        private final String anchorName;
        private final String value;
        private final DocCommentsTemplateData docComments;
        private final SortedSet<UsageInfoTemplateData> usageInfoList;
    }

    private final LinkedType linkedType;
    private final List<BitmaskValueTemplateData> values = new ArrayList<BitmaskValueTemplateData>();
}
