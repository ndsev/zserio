package zserio.emit.cpp;

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
import zserio.emit.cpp.types.CppNativeType;

public class ChoiceEmitterTemplateData extends CompoundTypeTemplateData
{
    public ChoiceEmitterTemplateData(TemplateDataContext context, ChoiceType choiceType)
            throws ZserioEmitException
    {
        super(context, choiceType);

        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);

        final Expression expression = choiceType.getSelectorExpression();
        selectorExpression = cppExpressionFormatter.formatGetter(expression);

        final CppNativeType expressionNativeType =
                cppNativeTypeMapper.getCppType(expression.getExprZserioType());
        selectorExpressionTypeName = expressionNativeType.getFullName();

        caseMemberList = new ArrayList<CaseMember>();
        final boolean withWriterCode = context.getWithWriterCode();
        final ExpressionFormatter cppIndirectExpressionFormatter =
                context.getOwnerIndirectExpressionFormatter(this);
        final Iterable<ChoiceCase> choiceCaseTypes = choiceType.getChoiceCases();
        for (ChoiceCase choiceCaseType : choiceCaseTypes)
        {
            caseMemberList.add(new CaseMember(cppNativeTypeMapper, choiceType, choiceCaseType,
                    cppExpressionFormatter, cppIndirectExpressionFormatter, this, withWriterCode));
        }

        final ChoiceDefault choiceDefaultType = choiceType.getChoiceDefault();
        if (choiceDefaultType != null)
        {
            defaultMember = new DefaultMember(cppNativeTypeMapper, choiceType, choiceDefaultType,
                    cppExpressionFormatter, cppIndirectExpressionFormatter, this, withWriterCode);
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
        public CaseMember(CppNativeTypeMapper cppNativeTypeMapper, ChoiceType choiceType,
                ChoiceCase choiceCaseType, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppIndirectExpressionFormatter, IncludeCollector includeCollector,
                boolean withWriterCode) throws ZserioEmitException
        {
            expressionList = new ArrayList<String>();
            final Iterable<ChoiceCaseExpression> caseExpressions = choiceCaseType.getExpressions();
            for (ChoiceCaseExpression caseExpression : caseExpressions)
                expressionList.add(cppExpressionFormatter.formatGetter(caseExpression.getExpression()));

            final Field fieldType = choiceCaseType.getField();
            compoundField = (fieldType != null) ? new CompoundFieldTemplateData(cppNativeTypeMapper,
                    choiceType, fieldType, cppExpressionFormatter,
                    cppIndirectExpressionFormatter, includeCollector, withWriterCode) : null;
        }

        public List<String> getExpressionList()
        {
            return expressionList;
        }

        public CompoundFieldTemplateData getCompoundField()
        {
            return compoundField;
        }

        private final List<String>              expressionList;
        private final CompoundFieldTemplateData compoundField;
    }

    public static class DefaultMember
    {
        public DefaultMember(CppNativeTypeMapper cppNativeTypeMapper,
                ChoiceType choiceType, ChoiceDefault choiceDefaultType,
                ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
                IncludeCollector includeCollector, boolean withWriterCode) throws ZserioEmitException
        {
            final Field fieldType = choiceDefaultType.getField();
            compoundField = (fieldType != null) ? new CompoundFieldTemplateData(
                    cppNativeTypeMapper, choiceType, fieldType, cppExpressionFormatter,
                    cppIndirectExpressionFormatter, includeCollector, withWriterCode) : null;
        }

        public CompoundFieldTemplateData getCompoundField()
        {
            return compoundField;
        }

        private final CompoundFieldTemplateData compoundField;
    }

    private final String                            selectorExpression;
    private final String                            selectorExpressionTypeName;
    private final List<CaseMember>                  caseMemberList;
    private final DefaultMember                     defaultMember;
    private final boolean                           isDefaultUnreachable;
}
