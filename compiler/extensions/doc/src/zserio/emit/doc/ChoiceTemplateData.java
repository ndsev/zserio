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

public class ChoiceTemplateData extends CompoundTypeTemplateData
{
    public ChoiceTemplateData(TemplateDataContext context, ChoiceType choiceType) throws ZserioEmitException
    {
        super(context, choiceType);

        final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
        selectorExpression = docExpressionFormatter.formatGetter(choiceType.getSelectorExpression());

        for (ChoiceCase choiceCase : choiceType.getChoiceCases())
        {
            caseMembers.add(new CaseMemberTemplateData(
                    choiceCase, choiceType.getSelectorExpression(), docExpressionFormatter));
        }

        defaultMember = choiceType.getChoiceDefault() != null
                ? new DefaultMemberTemplateData(choiceType.getChoiceDefault(), docExpressionFormatter)
                : null;
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

    public class CaseMemberTemplateData
    {
        public CaseMemberTemplateData(ChoiceCase choiceCase, Expression selectorExpression,
                ExpressionFormatter docExpressionFormatter) throws ZserioEmitException
        {
            caseList = new ArrayList<CaseTemplateData>();
            final Iterable<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
            for (ChoiceCaseExpression caseExpression : caseExpressions)
            {
                caseList.add(new CaseTemplateData(caseExpression.getExpression(),
                        caseExpression.getDocComment(), selectorExpression, docExpressionFormatter));
            }

            field = choiceCase.getField() != null
                    ? new FieldTemplateData(choiceCase.getField(), docExpressionFormatter)
                    : null;
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

    public class CaseTemplateData
    {
        public CaseTemplateData(Expression caseExpression, DocComment docComment, Expression selectorExpression,
                ExpressionFormatter docExpressionFormatter) throws ZserioEmitException
        {
            expression = docExpressionFormatter.formatGetter(caseExpression);
            this.docComment = new DocCommentTemplateData(docComment);

            final Object caseExpressionObject = caseExpression.getExprSymbolObject();
            final ZserioType selectorExpressionType = selectorExpression.getExprZserioType();
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

        public DocCommentTemplateData getDocComment()
        {
            return docComment;
        }

        public CaseSeeLinkTemplateData getSeeLink()
        {
            return seeLink;
        }

        private final String expression;
        private final DocCommentTemplateData docComment;
        private final CaseSeeLinkTemplateData seeLink;
    }

    public class CaseSeeLinkTemplateData
    {
        public CaseSeeLinkTemplateData(EnumItem caseType, EnumType caseTypeOwner) throws ZserioEmitException
        {
            text = caseTypeOwner.getName() + "." + caseType.getName();
            link = DocEmitterTools.getUrlNameFromType(caseTypeOwner) + "#casedef_" + caseType.getName();
        }

        public CaseSeeLinkTemplateData(BitmaskValue caseType, BitmaskType caseTypeOwner) throws ZserioEmitException
        {
            text = caseTypeOwner.getName() + "." + caseType.getName();
            link = DocEmitterTools.getUrlNameFromType(caseTypeOwner) + "#casedef_" + caseType.getName();
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

    public class DefaultMemberTemplateData
    {
        public DefaultMemberTemplateData(ChoiceDefault choiceDefault,
                ExpressionFormatter docExpressionFormatter) throws ZserioEmitException
        {
            field = choiceDefault.getField() != null
                    ? new FieldTemplateData(choiceDefault.getField(), docExpressionFormatter)
                    : null;
            docComment = new DocCommentTemplateData(choiceDefault.getDocComment());
        }

        public FieldTemplateData getField()
        {
            return field;
        }

        public DocCommentTemplateData getDocComment()
        {
            return docComment;
        }

        private final FieldTemplateData field;
        private final DocCommentTemplateData docComment;
    }

    private final String selectorExpression;
    private final List<CaseMemberTemplateData> caseMembers = new ArrayList<CaseMemberTemplateData>();
    private final DefaultMemberTemplateData defaultMember;
}