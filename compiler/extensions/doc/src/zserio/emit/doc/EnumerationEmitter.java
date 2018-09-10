package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ZserioException;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.ZserioType;
import zserio.emit.common.ExpressionFormatter;
import zserio.tools.HashUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class EnumerationEmitter extends DefaultHtmlEmitter
{
    private EnumType enumeration;
    private DocCommentTemplateData docCommentData;
    private final List<EnumItemTemplateData> items = new ArrayList<EnumItemTemplateData>();
    private String docPath;
    private boolean withSvgDiagrams;

    public EnumerationEmitter(String outputPath, boolean withSvgDiagrams)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
    }

    public void emit(EnumType e)
    {
        this.enumeration = e;
        docCommentData = new DocCommentTemplateData(e.getDocComment());
        items.clear();
        for (EnumItem item : e.getItems())
        {
            items.add(new EnumItemTemplateData(item));
        }
        containers.clear();
        for (CompoundType compound : enumeration.getUsedByCompoundList())
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
        catch (IOException exc)
        {
            throw new ZserioException(exc);
        }
        catch (TemplateException exc)
        {
            throw new ZserioException(exc);
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

        return enumeration.getScope().getPackage().getPackageName();
    }

    public EnumType getType()
    {
        return enumeration;
    }

    public String getEnumType()
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
        private EnumItem enumItem;
        private Expression usedByExpression;

        public UsageInfoEmitter( EnumItem   enumItem,
                                 Expression usedByExpression )
        {
            this.enumItem = enumItem;
            this.usedByExpression = usedByExpression;
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

        public Expression getExpression()
        {
            return usedByExpression;
        }

        private ChoiceType getChoiceType()
        {
            // by manual investigation the fixed hangout of the choiceType of a
            // expression within a "choice-case-item" can be resolved as follows
            final ZserioType type = getExpression().getScope().getOwner();
            return (type instanceof ChoiceType) ? (ChoiceType)type : null;
        }

        public boolean getIsFromChoiceCase()
        {
            return getChoiceType()!=null;
        }

        public String getChoiceCaseLinkText()
        {
            ChoiceType ct = getChoiceType();
            return(
                ct.getName() + "( " + getEnumItem().getName() + " )"
            );
        }

        public String getChoiceCaseLink()
        {
            ChoiceType ct = getChoiceType();
            return DocEmitterTools.getUrlNameFromType(ct) + "#casedef_" + getEnumItem().getName();
        }
    }; // class usageInfoEmitter

    public Iterable< EnumItemTemplateData > getItems()
    {
        return items;
    }

    public String getCollaborationDiagramSvgFileName()
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, enumeration)
                                      : null;
    }

    public static class EnumItemTemplateData
    {
        public EnumItemTemplateData(EnumItem enumItem)
        {
            name = enumItem.getName();

            final DocExpressionFormattingPolicy policy = new DocExpressionFormattingPolicy();
            final ExpressionFormatter expressionFormatter = new ExpressionFormatter(policy);
            final Expression valueExpression = enumItem.getValueExpression();
            value = (valueExpression == null) ? enumItem.getValue().toString() :
                expressionFormatter.formatGetter(enumItem.getValueExpression());

            docCommentData = new DocCommentTemplateData(enumItem.getDocComment());

            usageInfoList = new TreeSet<UsageInfoEmitter>();
            for (Expression expression : enumItem.getUsedByExpressionList())
            {
                final UsageInfoEmitter usageInfoEmitter = new UsageInfoEmitter(enumItem, expression);
                /* This hack is necessary not to have duplicit entries. */
                if (usageInfoEmitter.getIsFromChoiceCase())
                    usageInfoList.add(new UsageInfoEmitter(enumItem, expression));
            }
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
