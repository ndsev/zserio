package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceDefault;
import zserio.ast.ChoiceType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.JavaNativeType;

public final class ChoiceEmitterTemplateData extends CompoundTypeTemplateData
{
    public ChoiceEmitterTemplateData(TemplateDataContext context, ChoiceType choiceType)
            throws ZserioEmitException
    {
        super(context, choiceType);

        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
        final Expression expression = choiceType.getSelectorExpression();
        selectorExpression = javaExpressionFormatter.formatGetter(expression);

        final JavaNativeTypeMapper javaNativeTypeMapper = context.getJavaNativeTypeMapper();
        final JavaNativeType expressionNativeType =
                javaNativeTypeMapper.getJavaType(expression.getExprZserioType());
        selectorExpressionTypeName = expressionNativeType.getFullName();

        final boolean withWriterCode = context.getWithWriterCode();
        final boolean withRangeCheckCode = context.getWithRangeCheckCode();
        caseMemberList = new ArrayList<CaseMember>();
        final Iterable<ChoiceCase> choiceCaseTypes = choiceType.getChoiceCases();
        for (ChoiceCase choiceCaseType : choiceCaseTypes)
        {
            caseMemberList.add(new CaseMember(javaNativeTypeMapper, withWriterCode, withRangeCheckCode,
                    choiceType, choiceCaseType, javaExpressionFormatter,
                    context.getJavaCaseExpressionFormatter()));
        }

        final ChoiceDefault choiceDefaultType = choiceType.getChoiceDefault();
        if (choiceDefaultType != null)
        {
            defaultMember = new DefaultMember(javaNativeTypeMapper, withWriterCode, withRangeCheckCode,
                    choiceType, choiceDefaultType, javaExpressionFormatter);
        }
        else
        {
            defaultMember = null;
        }

        isDefaultUnreachable = choiceType.isChoiceDefaultUnreachable();
    }

    public String getSelectorExpression()
    {
        return selectorExpression;
    }

    public String getSelectorExpressionTypeName()
    {
        return selectorExpressionTypeName;
    }

    public Iterable<CaseMember> getCaseMemberList()
    {
        return caseMemberList;
    }

    public DefaultMember getDefaultMember()
    {
        return defaultMember;
    }

    public boolean getIsDefaultUnreachable()
    {
        return isDefaultUnreachable;
    }

    public static class CaseMember
    {
        public CaseMember(JavaNativeTypeMapper javaNativeTypeMapper, boolean withWriterCode,
                boolean withRangeCheckCode, ChoiceType choiceType, ChoiceCase choiceCaseType,
                ExpressionFormatter javaExpressionFormatter, ExpressionFormatter javaCaseExpressionFormatter)
                        throws ZserioEmitException
        {
            caseList = new ArrayList<Case>();
            final Iterable<ChoiceCaseExpression> caseExpressions = choiceCaseType.getExpressions();
            for (ChoiceCaseExpression caseExpression : caseExpressions)
                caseList.add(new Case(javaExpressionFormatter, javaCaseExpressionFormatter,
                        caseExpression.getExpression()));

            final Field fieldType = choiceCaseType.getField();
            compoundField = (fieldType != null) ? new CompoundFieldTemplateData(javaNativeTypeMapper,
                    withWriterCode, withRangeCheckCode,  choiceType, fieldType, javaExpressionFormatter) :
                        null;
        }

        public Iterable<Case> getCaseList()
        {
            return caseList;
        }

        public CompoundFieldTemplateData getCompoundField()
        {
            return compoundField;
        }

        public static class Case
        {
            public Case(ExpressionFormatter javaExpressionFormatter,
                    ExpressionFormatter javaCaseExpressionFormatter, Expression choiceExpression)
                            throws ZserioEmitException
            {
                expressionForIf = javaExpressionFormatter.formatGetter(choiceExpression);
                expressionForCase = javaCaseExpressionFormatter.formatGetter(choiceExpression);
            }

            public String getExpressionForIf()
            {
                return expressionForIf;
            }

            public String getExpressionForCase()
            {
                return expressionForCase;
            }

            public final String expressionForIf;
            public final String expressionForCase;
        }

        private final List<Case>                caseList;
        private final CompoundFieldTemplateData compoundField;
    }

    public static class DefaultMember
    {
        public DefaultMember(JavaNativeTypeMapper javaNativeTypeMapper, boolean withWriterCode,
                boolean withRangeCheckCode, ChoiceType choiceType, ChoiceDefault choiceDefaultType,
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            final Field fieldType = choiceDefaultType.getField();
            compoundField = (fieldType != null) ? new CompoundFieldTemplateData(javaNativeTypeMapper,
                    withWriterCode, withRangeCheckCode, choiceType, fieldType, javaExpressionFormatter) :
                        null;
        }

        public CompoundFieldTemplateData getCompoundField()
        {
            return compoundField;
        }

        private final CompoundFieldTemplateData compoundField;
    }

    private final String            selectorExpression;
    private final String            selectorExpressionTypeName;
    private final List<CaseMember>  caseMemberList;
    private final DefaultMember     defaultMember;
    private final boolean           isDefaultUnreachable;
}
