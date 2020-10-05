package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceDefault;
import zserio.ast.ChoiceType;
import zserio.ast.DocComment;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.ZserioType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.StringJoinUtil;

public class ChoiceTemplateData extends CompoundTypeTemplateData
{
    public ChoiceTemplateData(TemplateDataContext context, ChoiceType choiceType) throws ZserioEmitException
    {
        super(context, choiceType);

        final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
        selectorExpression = docExpressionFormatter.formatGetter(choiceType.getSelectorExpression());

        caseMembers = new ArrayList<CaseMemberTemplateData>();
        for (ChoiceCase choiceCase : choiceType.getChoiceCases())
            caseMembers.add(new CaseMemberTemplateData(context, choiceType, choiceCase));

        defaultMember = (choiceType.getChoiceDefault() != null) ? new DefaultMemberTemplateData(context,
                choiceType) : null;
    }

    public String getSelectorExpression()
    {
         return selectorExpression;
    }

    public Iterable<CaseMemberTemplateData> getCaseMemberList()
    {
        return caseMembers;
    }

    public DefaultMemberTemplateData getDefaultMember()
    {
        return defaultMember;
    }

    public static class CaseMemberTemplateData
    {
        public CaseMemberTemplateData(TemplateDataContext context, ChoiceType choiceType, ChoiceCase choiceCase)
                throws ZserioEmitException
        {
            caseList = new ArrayList<CaseTemplateData>();
            final Iterable<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
            for (ChoiceCaseExpression caseExpression : caseExpressions)
                caseList.add(new CaseTemplateData(context, choiceType, caseExpression.getExpression(),
                        caseExpression.getDocComments()));

            field = (choiceCase.getField() != null) ? new FieldTemplateData(context, choiceType,
                    choiceCase.getField()) : null;
        }

        public Iterable<CaseTemplateData> getCaseList()
        {
            return caseList;
        }

        public FieldTemplateData getField()
        {
            return field;
        }

        private final List<CaseTemplateData> caseList;
        private final FieldTemplateData field;
    }

    public static class CaseTemplateData
    {
        public CaseTemplateData(TemplateDataContext context, ChoiceType choiceType, Expression caseExpression,
                List<DocComment> docComments) throws ZserioEmitException
        {
            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
            expression = docExpressionFormatter.formatGetter(caseExpression);
            definitionAnchorName = DocEmitterTools.getAnchorName(choiceType, "casedef", expression);
            detailAnchorName = DocEmitterTools.getAnchorName(choiceType, "case", expression);

            this.docComments = new DocCommentsTemplateData(context, docComments);

            final Object caseExpressionObject = caseExpression.getExprSymbolObject();
            final ZserioType selectorExpressionType = choiceType.getSelectorExpression().getExprZserioType();
            if (caseExpressionObject instanceof EnumItem  && selectorExpressionType instanceof EnumType)
            {
                seeLink = new CaseSeeLinkTemplateData((EnumItem)caseExpressionObject,
                        (EnumType)selectorExpressionType);
            }
            else if (caseExpressionObject instanceof BitmaskValue &&
                    selectorExpressionType instanceof BitmaskType)
            {
                seeLink = new CaseSeeLinkTemplateData((BitmaskValue)caseExpressionObject,
                        (BitmaskType)selectorExpressionType);
            }
            else
            {
                seeLink = null;
            }
        }

        public String getExpression()
        {
            return expression;
        }

        public String getDefinitionAnchorName()
        {
            return definitionAnchorName;
        }

        public String getDetailAnchorName()
        {
            return detailAnchorName;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        public CaseSeeLinkTemplateData getSeeLink()
        {
            return seeLink;
        }

        private final String expression;
        private final String definitionAnchorName;
        private final String detailAnchorName;
        private final DocCommentsTemplateData docComments;
        private final CaseSeeLinkTemplateData seeLink;
    }

    public static class CaseSeeLinkTemplateData
    {
        public CaseSeeLinkTemplateData(EnumItem caseType, EnumType caseTypeOwner) throws ZserioEmitException
        {
            text = caseTypeOwner.getName() + "." + caseType.getName();
            final String urlName = DocEmitterTools.getUrlNameFromType(caseTypeOwner);
            final String anchorName = DocEmitterTools.getAnchorName(caseTypeOwner, caseType.getName());
            link = StringJoinUtil.joinStrings(urlName, anchorName, "#");
        }

        public CaseSeeLinkTemplateData(BitmaskValue caseType, BitmaskType caseTypeOwner)
                throws ZserioEmitException
        {
            text = caseTypeOwner.getName() + "." + caseType.getName();
            final String urlName = DocEmitterTools.getUrlNameFromType(caseTypeOwner);
            final String anchorName = DocEmitterTools.getAnchorName(caseTypeOwner, caseType.getName());
            link = StringJoinUtil.joinStrings(urlName, anchorName, "#");
        }

        public String getText()
        {
            return text;
        }

        public String getLink()
        {
            return link;
        }

        private final String text;
        private final String link;
    }

    public static class DefaultMemberTemplateData
    {
        public DefaultMemberTemplateData(TemplateDataContext context, ChoiceType choiceType)
                throws ZserioEmitException
        {
            final ChoiceDefault choiceDefault = choiceType.getChoiceDefault();
            field = (choiceDefault.getField() == null) ? null : new FieldTemplateData(context, choiceType,
                    choiceDefault.getField());
            docComments = new DocCommentsTemplateData(context, choiceDefault.getDocComments());
        }

        public FieldTemplateData getField()
        {
            return field;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final FieldTemplateData field;
        private final DocCommentsTemplateData docComments;
    }

    private final String selectorExpression;
    private final List<CaseMemberTemplateData> caseMembers;
    private final DefaultMemberTemplateData defaultMember;
}
