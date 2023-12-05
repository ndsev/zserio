package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceDefault;
import zserio.ast.ChoiceType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for ChoiceEmitter.
 */
public final class ChoiceEmitterTemplateData extends CompoundTypeTemplateData
{
    public ChoiceEmitterTemplateData(TemplateDataContext context, ChoiceType choiceType)
            throws ZserioExtensionException
    {
        super(context, choiceType);

        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        final Expression selectorExpression = choiceType.getSelectorExpression();
        selector = pythonExpressionFormatter.formatGetter(selectorExpression);

        caseMemberList = new ArrayList<CaseMember>();
        final Iterable<ChoiceCase> choiceCases = choiceType.getChoiceCases();
        for (ChoiceCase choiceCase : choiceCases)
        {
            caseMemberList.add(new CaseMember(
                    context, choiceType, choiceCase, pythonExpressionFormatter, this));
        }

        final ChoiceDefault choiceDefault = choiceType.getChoiceDefault();
        defaultMember = (choiceDefault == null) ? null:
                new DefaultMember(context, choiceType, choiceDefault, this);

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

    public static final class CaseMember
    {
        public CaseMember(TemplateDataContext context, ChoiceType choiceType, ChoiceCase choiceCase,
                ExpressionFormatter expressionFormatter, ImportCollector importCollector)
                        throws ZserioExtensionException
        {
            expressionList = new ArrayList<String>();
            final Iterable<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
            for (ChoiceCaseExpression caseExpression : caseExpressions)
                expressionList.add(expressionFormatter.formatGetter(caseExpression.getExpression()));

            final Field fieldType = choiceCase.getField();
            compoundField = (fieldType != null) ?
                    new CompoundFieldTemplateData(context, choiceType, fieldType, importCollector) : null;
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

    public static final class DefaultMember
    {
        public DefaultMember(TemplateDataContext context, ChoiceType choiceType, ChoiceDefault choiceDefault,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            final Field fieldType = choiceDefault.getField();
            compoundField = (fieldType != null) ?
                    new CompoundFieldTemplateData(context, choiceType, fieldType, importCollector) : null;
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
