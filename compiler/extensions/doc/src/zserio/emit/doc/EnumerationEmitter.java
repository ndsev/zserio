package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.ZserioType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.HashUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class EnumerationEmitter extends DefaultHtmlEmitter
{
    private EnumType enumeration;
    private DocCommentTemplateData docCommentData;
    private final List<EnumItemTemplateData> items = new ArrayList<EnumItemTemplateData>();
    private final String docPath;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;

    public EnumerationEmitter(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
    }

    public void emit(EnumType e) throws ZserioEmitException
    {
        this.enumeration = e;
        docCommentData = new DocCommentTemplateData(e.getDocComment());
        items.clear();
        for (EnumItem item : e.getItems())
        {
            items.add(new EnumItemTemplateData(item, usedByCollector));
        }
        containers.clear();
        for (CompoundType compound : usedByCollector.getUsedByTypes(enumeration, CompoundType.class))
        {
            CompoundEmitter ce = new CompoundEmitter(compound);
            containers.add(ce);
        }
        services.clear();

        try
        {
            Template tpl = cfg.getTemplate("doc/enumeration.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(e);
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
        if (enumeration == null)
            throw new RuntimeException("getPackageName() called before emit()!");

        return enumeration.getPackage().getPackageName().toString();
    }

    public EnumType getType()
    {
        return enumeration;
    }

    public String getEnumType() throws ZserioEmitException
    {
        if (enumeration == null)
            throw new RuntimeException("getEnumType() called before emit()!");

        ZserioType enumType = enumeration.getEnumType();
        String enumTypeName = TypeNameEmitter.getTypeName(enumType);
        return enumTypeName;
    }

    public DocCommentTemplateData getDocComment()
    {
        return docCommentData;
    }

    public static class UsageInfoEmitter implements Comparable<UsageInfoEmitter>
    {
        private final EnumItem enumItem;
        private final ChoiceType choiceType;

        public UsageInfoEmitter(EnumItem enumItem, ChoiceType choiceType)
        {
            this.enumItem = enumItem;
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
    }; // class usageInfoEmitter

    public Iterable< EnumItemTemplateData > getItems()
    {
        return items;
    }

    public String getCollaborationDiagramSvgFileName() throws ZserioEmitException
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, enumeration)
                                      : null;
    }

    public static class EnumItemTemplateData
    {
        public EnumItemTemplateData(EnumItem enumItem, UsedByCollector usedByCollector)
                throws ZserioEmitException
        {
            name = enumItem.getName();

            final DocExpressionFormattingPolicy policy = new DocExpressionFormattingPolicy();
            final ExpressionFormatter expressionFormatter = new ExpressionFormatter(policy);
            final Expression valueExpression = enumItem.getValueExpression();
            value = (valueExpression == null) ? enumItem.getValue().toString() :
                expressionFormatter.formatGetter(enumItem.getValueExpression());

            docCommentData = new DocCommentTemplateData(enumItem.getDocComment());

            usageInfoList = new TreeSet<UsageInfoEmitter>();
            for (ChoiceType choiceType : usedByCollector.getUsedByChoices(enumItem))
                usageInfoList.add(new UsageInfoEmitter(enumItem, choiceType));
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
