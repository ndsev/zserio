package zserio.extension.java;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceDefault;
import zserio.ast.ChoiceType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for ChoiceEmitter.
 */
public final class ChoiceEmitterTemplateData extends CompoundTypeTemplateData
{
    public ChoiceEmitterTemplateData(TemplateDataContext context, ChoiceType choiceType)
            throws ZserioExtensionException
    {
        super(context, choiceType);

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();

        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
        final Expression expression = choiceType.getSelectorExpression();
        selectorExpression = javaExpressionFormatter.formatGetter(expression);
        isSelectorExpressionBoolean = expression.getExprType() == Expression.ExpressionType.BOOLEAN;
        final BigInteger selectorUpperBound = expression.getIntegerUpperBound();
        isSelectorExpressionBigInteger = expression.needsBigInteger();
        isSelectorExpressionLong = (isSelectorExpressionBigInteger == false && selectorUpperBound != null &&
                selectorUpperBound.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0);
        selectorExpressionBitmaskTypeName = createSelectorExpressionBitmaskType(expression, javaNativeMapper);

        caseMemberList = new ArrayList<CaseMember>();
        final Iterable<ChoiceCase> choiceCaseTypes = choiceType.getChoiceCases();
        for (ChoiceCase choiceCaseType : choiceCaseTypes)
            caseMemberList.add(new CaseMember(context, choiceType, choiceCaseType));

        final ChoiceDefault choiceDefaultType = choiceType.getChoiceDefault();
        if (choiceDefaultType != null)
        {
            defaultMember = new DefaultMember(context, choiceType, choiceDefaultType);
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

    public boolean getIsSelectorExpressionBoolean()
    {
        return isSelectorExpressionBoolean;
    }

    public boolean getIsSelectorExpressionBigInteger()
    {
        return isSelectorExpressionBigInteger;
    }

    public boolean getIsSelectorExpressionLong()
    {
        return isSelectorExpressionLong;
    }

    public String getSelectorExpressionBitmaskTypeName()
    {
        return selectorExpressionBitmaskTypeName;
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

    public static final class CaseMember
    {
        public CaseMember(TemplateDataContext context, ChoiceType choiceType, ChoiceCase choiceCaseType)
                throws ZserioExtensionException
        {
            caseList = new ArrayList<Case>();
            final Iterable<ChoiceCaseExpression> caseExpressions = choiceCaseType.getExpressions();
            for (ChoiceCaseExpression caseExpression : caseExpressions)
                caseList.add(new Case(context, caseExpression.getExpression()));

            final Field fieldType = choiceCaseType.getField();
            compoundField =
                    (fieldType != null) ? new CompoundFieldTemplateData(context, choiceType, fieldType) : null;
        }

        public Iterable<Case> getCaseList()
        {
            return caseList;
        }

        public CompoundFieldTemplateData getCompoundField()
        {
            return compoundField;
        }

        public static final class Case
        {
            public Case(TemplateDataContext context, Expression choiceExpression)
                    throws ZserioExtensionException
            {
                final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
                final ExpressionFormatter javaCaseExpressionFormatter =
                        context.getJavaCaseExpressionFormatter();
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

        private final List<Case> caseList;
        private final CompoundFieldTemplateData compoundField;
    }

    public static final class DefaultMember
    {
        public DefaultMember(TemplateDataContext context, ChoiceType choiceType,
                ChoiceDefault choiceDefaultType) throws ZserioExtensionException
        {
            final Field fieldType = choiceDefaultType.getField();
            compoundField =
                    (fieldType != null) ? new CompoundFieldTemplateData(context, choiceType, fieldType) : null;
        }

        public CompoundFieldTemplateData getCompoundField()
        {
            return compoundField;
        }

        private final CompoundFieldTemplateData compoundField;
    }

    private String createSelectorExpressionBitmaskType(Expression expr, JavaNativeMapper javaNativeMapper)
            throws ZserioExtensionException
    {
        if (expr.getExprType() != Expression.ExpressionType.BITMASK)
            return null;

        if (!(expr.getExprZserioType() instanceof BitmaskType))
            throw new ZserioExtensionException("Missing expression ZseiroType for bitmask!");

        final JavaNativeType nativeType = javaNativeMapper.getJavaType(expr.getExprZserioType());
        return nativeType.getFullName();
    }

    private final String selectorExpression;
    private final boolean isSelectorExpressionBoolean;
    private final boolean isSelectorExpressionBigInteger;
    private final boolean isSelectorExpressionLong;
    private final String selectorExpressionBitmaskTypeName;
    private final List<CaseMember> caseMemberList;
    private final DefaultMember defaultMember;
    private final boolean isDefaultUnreachable;
}
