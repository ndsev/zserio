package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Expression;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.HashUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class BitmaskEmitter extends DefaultHtmlEmitter
{
    private BitmaskType bitmaskType;
    private DocCommentTemplateData docCommentData;
    private final List<BitmaskValueTemplateData> values = new ArrayList<BitmaskValueTemplateData>();
    private final String docPath;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;

    public BitmaskEmitter(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
    }

    public void emit(BitmaskType bitmaskType) throws ZserioEmitException
    {
        this.bitmaskType = bitmaskType;
        docCommentData = new DocCommentTemplateData(bitmaskType.getDocComment());
        values.clear();
        for (BitmaskValue value : bitmaskType.getValues())
        {
            values.add(new BitmaskValueTemplateData(value, usedByCollector));
        }
        containers.clear();
        for (CompoundType compound : usedByCollector.getUsedByTypes(bitmaskType, CompoundType.class))
        {
            CompoundEmitter ce = new CompoundEmitter(compound);
            containers.add(ce);
        }
        protocols.clear();

        try
        {
            Template tpl = cfg.getTemplate("doc/bitmask.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(bitmaskType);
            tpl.process(this, writer);
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    @Override
    public String getPackageName()
    {
        if (bitmaskType == null)
            throw new RuntimeException("getPackageName() called before emit()!");

        return bitmaskType.getPackage().getPackageName().toString();
    }

    public BitmaskType getType()
    {
        return bitmaskType;
    }

    public String getBitmaskType() throws ZserioEmitException
    {
        if (bitmaskType == null)
            throw new RuntimeException("getBitamskType() called before emit()!");

        TypeInstantiation bitmaskTypeInstantiation = bitmaskType.getTypeInstantiation();
        String bitmaskTypeName = TypeNameEmitter.getTypeName(bitmaskTypeInstantiation);
        return bitmaskTypeName;
    }

    public DocCommentTemplateData getDocComment()
    {
        return docCommentData;
    }

    public static class UsageInfoEmitter implements Comparable<UsageInfoEmitter>
    {
        private final BitmaskValue bitmaskValue;
        private final ChoiceType choiceType;

        public UsageInfoEmitter(BitmaskValue bitmaskValue, ChoiceType choiceType)
        {
            this.bitmaskValue = bitmaskValue;
            this.choiceType = choiceType;
        }

        /* Don't change this ordering to have always the same generated HTML sources. */
        @Override
        public int compareTo(UsageInfoEmitter other)
        {
            return getChoiceCaseLinkText().compareTo(other.getChoiceCaseLinkText());
        }

        @Override
        public boolean equals(Object other)
        {
            if ( !(other instanceof UsageInfoEmitter) )
                return false;

            return (this == other) || compareTo((UsageInfoEmitter)other) == 0;
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
            return DocEmitterTools.getUrlNameFromType(choiceType) + "#casedef_" + getBitmaskValue().getName();
        }
    }; // class usageInfoEmitter

    public Iterable< BitmaskValueTemplateData > getValues()
    {
        return values;
    }

    public String getCollaborationDiagramSvgFileName() throws ZserioEmitException
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, bitmaskType)
                                      : null;
    }

    public static class BitmaskValueTemplateData
    {
        public BitmaskValueTemplateData(BitmaskValue bitmaskValue, UsedByCollector usedByCollector)
                throws ZserioEmitException
        {
            name = bitmaskValue.getName();

            final DocExpressionFormattingPolicy policy = new DocExpressionFormattingPolicy();
            final ExpressionFormatter expressionFormatter = new ExpressionFormatter(policy);
            final Expression valueExpression = bitmaskValue.getValueExpression();
            value = (valueExpression == null) ? bitmaskValue.getValue().toString() :
                expressionFormatter.formatGetter(bitmaskValue.getValueExpression());

            docCommentData = new DocCommentTemplateData(bitmaskValue.getDocComment());

            usageInfoList = new TreeSet<UsageInfoEmitter>();
            for (ChoiceType choiceType : usedByCollector.getUsedByChoices(bitmaskValue))
                usageInfoList.add(new UsageInfoEmitter(bitmaskValue, choiceType));
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        public DocCommentTemplateData getDocCommentData()
        {
            return docCommentData;
        }

        public Iterable<UsageInfoEmitter> getUsageInfoList()
        {
            return usageInfoList;
        }

        private final String name;
        private final String value;
        private final DocCommentTemplateData docCommentData;
        private final SortedSet<UsageInfoEmitter> usageInfoList;
    }
};
