package zserio.emit.python;

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

public class ChoiceEmitterTemplateData extends CompoundTypeTemplateData
{
    public ChoiceEmitterTemplateData(TemplateDataContext context, ChoiceType choiceType)
            throws ZserioEmitException
    {
        super(context, choiceType);

        final PythonNativeTypeMapper pythonNativeTypeMapper = context.getPythonNativeTypeMapper();

        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        final Expression selectorExpression = choiceType.getSelectorExpression();
        selector = pythonExpressionFormatter.formatGetter(selectorExpression);

        caseMemberList = new ArrayList<CaseMember>();
        final Iterable<ChoiceCase> choiceCases = choiceType.getChoiceCases();
        for (ChoiceCase choiceCase : choiceCases)
        {
            caseMemberList.add(new CaseMember(choiceType, choiceCase, pythonNativeTypeMapper,
                    getWithRangeCheckCode(), pythonExpressionFormatter, this));
        }

        final ChoiceDefault choiceDefault = choiceType.getChoiceDefault();
        if (choiceDefault != null)
        {
            defaultMember = new DefaultMember(choiceType, choiceDefault, pythonNativeTypeMapper,
                    getWithRangeCheckCode(), pythonExpressionFormatter, this);
        }
        else
        {
            defaultMember = null;
        }

        isDefaultUnreachable = choiceType.isChoiceDefaultUnreachable();
    }

    public String getSelector()
    {
        return selector;
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
        public CaseMember(ChoiceType choiceType, ChoiceCase choiceCase,
                PythonNativeTypeMapper pythonNativeTypeMapper, boolean withRangeCheckCode,
                ExpressionFormatter expressionFormatter, ImportCollector importCollector)
                        throws ZserioEmitException
        {
            expressionList = new ArrayList<String>();
            final Iterable<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
            for (ChoiceCaseExpression caseExpression : caseExpressions)
                expressionList.add(expressionFormatter.formatGetter(caseExpression.getExpression()));

            final Field fieldType = choiceCase.getField();
            compoundField = (fieldType != null) ?
                    new CompoundFieldTemplateData(pythonNativeTypeMapper, withRangeCheckCode, choiceType,
                            fieldType, expressionFormatter, importCollector) : null;
        }

        public Iterable<String> getExpressionList()
        {
            return expressionList;
        }

        public CompoundFieldTemplateData getCompoundField()
        {
            return compoundField;
        }

        private final List<String> expressionList;
        private final CompoundFieldTemplateData compoundField;
    }

    public static class DefaultMember
    {
        public DefaultMember(ChoiceType choiceType, ChoiceDefault choiceDefault,
                PythonNativeTypeMapper pythonNativeTypeMapper, boolean withRangeCheckCode,
                ExpressionFormatter expressionFormatter, ImportCollector importCollector)
                        throws ZserioEmitException
        {
            final Field fieldType = choiceDefault.getField();
            compoundField = (fieldType != null) ?
                    new CompoundFieldTemplateData(pythonNativeTypeMapper, withRangeCheckCode, choiceType,
                            fieldType, expressionFormatter, importCollector) : null;
        }

        public CompoundFieldTemplateData getCompoundField()
        {
            return compoundField;
        }

        private final CompoundFieldTemplateData compoundField;
    }

    private final String selector;
    private final List<CaseMember> caseMemberList;
    private final DefaultMember defaultMember;
    private final boolean isDefaultUnreachable;
}
