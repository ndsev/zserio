package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for Choice types.
 *
 * Choice types are Zserio types as well.
 */
public class ChoiceType extends CompoundType
{
    /**
     * Constructor.
     *
     * @param token              ANTLR4 token to localize AST node in the sources.
     * @param pkg                Package to which belongs the choice type.
     * @param name               Name of the choice type.
     * @param parameters         List of parameters for the choice type.
     * @param selectorExpression Selector expression of the choice type.
     * @param choiceCases        List of all choice cases.
     * @param choiceDefault      Choice default case or null if default case is not defined.
     * @param functions          List of all functions of the choice type.
     * @param docComment         Documentation comment belonging to this node.
     */
    public ChoiceType(Token token, Package pkg, String name, List<Parameter> parameters,
            Expression selectorExpression, List<ChoiceCase> choiceCases, ChoiceDefault choiceDefault,
            List<FunctionType> functions, DocComment docComment)
    {
        super(token, pkg, name, parameters, getChoiceFields(choiceCases, choiceDefault), functions, docComment);

        this.selectorExpression = selectorExpression;
        this.choiceCases = choiceCases;
        this.choiceDefault = choiceDefault;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitChoiceType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (Parameter parameter : getParameters())
            parameter.accept(visitor);

        selectorExpression.accept(visitor);

        for (ChoiceCase choiceCase : choiceCases)
            choiceCase.accept(visitor);

        if (choiceDefault != null)
            choiceDefault.accept(visitor);

        for (FunctionType function : getFunctions())
            function.accept(visitor);

        if (getDocComment() != null)
            getDocComment().accept(visitor);
    }

    /**
     * Gets selector expression.
     *
     * Selector expression is compulsory for choice types, therefore this method cannot return null.
     *
     * @return Returns expressions which is given as choice selector.
     */
    public Expression getSelectorExpression()
    {
        return selectorExpression;
    }

    /**
     * Gets list of choice cases defined by the choice.
     *
     * @return List of choice cases.
     */
    public List<ChoiceCase> getChoiceCases()
    {
        return Collections.unmodifiableList(choiceCases);
    }

    /**
     * Gets default case defined by the choice.
     *
     * @return Default case or null if default case is not defined.
     */
    public ChoiceDefault getChoiceDefault()
    {
        return choiceDefault;
    }

    /**
     * Checks if default case in choice can happen.
     *
     * Actually, only boolean choices can have default case unreachable. This can happen only if the boolean
     * choice has defined both cases (true and false).
     *
     * @return Returns true if default case is unreachable.
     */
    public boolean isChoiceDefaultUnreachable()
    {
        return isChoiceDefaultUnreachable;
    }

    private static List<Field> getChoiceFields(List<ChoiceCase> choiceCases, ChoiceDefault choiceDefault)
    {
        final List<Field> fields = new ArrayList<Field>();
        for (ChoiceCase choiceCase : choiceCases)
        {
            if (choiceCase.getField() != null)
                fields.add(choiceCase.getField());
        }

        if (choiceDefault != null && choiceDefault.getField() != null)
            fields.add(choiceDefault.getField());

        return fields;
    }

    @Override
    void check()
    {
        super.check();
        checkTableFields();

        isChoiceDefaultUnreachable = checkUnreachableDefault();
        checkSelectorType();
        checkCaseTypes();
        checkEnumerationCases();
        checkDuplicatedCases();
    }

    private boolean checkUnreachableDefault()
    {
        boolean isDefaulUnreachable = false;
        if (selectorExpression.getExprType() == Expression.ExpressionType.BOOLEAN && getNumCases() > 1)
        {
            if (choiceDefault != null)
                throw new ParserException(choiceDefault, "Choice '" + getName() +
                        "' has unreachable default case!");

            isDefaulUnreachable = true;
        }

        return isDefaulUnreachable;
    }

    private int getNumCases()
    {
        int numCases = 0;
        for (ChoiceCase choiceCase : choiceCases)
            numCases += choiceCase.getExpressions().size();

        return numCases;
    }

    private void checkSelectorType()
    {
        final Expression.ExpressionType selectorExpressionType = selectorExpression.getExprType();
        if (selectorExpressionType != Expression.ExpressionType.INTEGER &&
            selectorExpressionType != Expression.ExpressionType.BOOLEAN &&
            selectorExpressionType != Expression.ExpressionType.ENUM)
            throw new ParserException(this, "Choice '" + getName() + "' uses forbidden " +
                    selectorExpressionType.name() + " selector!");
    }

    private void checkCaseTypes()
    {
        final Expression.ExpressionType selectorExpressionType = selectorExpression.getExprType();
        for (ChoiceCase choiceCase : choiceCases)
        {
            final List<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
            for (ChoiceCaseExpression caseExpression : caseExpressions)
            {
                final Expression expression = caseExpression.getExpression();
                if (expression.getExprType() != selectorExpressionType)
                    throw new ParserException(expression, "Choice '" + getName() +
                            "' has incompatible case type!");

                if (!expression.getReferencedSymbolObjects(Parameter.class).isEmpty())
                    throw new ParserException(expression, "Choice '" + getName() +
                            "' has non-constant case expression!");
            }
        }
    }

    private void checkDuplicatedCases()
    {
        final List<Expression> allExpressions = new ArrayList<Expression>();
        for (ChoiceCase choiceCase : choiceCases)
        {
            final List<ChoiceCaseExpression> newCaseExpressions = choiceCase.getExpressions();
            for (ChoiceCaseExpression newCaseExpression : newCaseExpressions)
            {
                for (Expression caseExpression : allExpressions)
                {
                    final Expression newExpression = newCaseExpression.getExpression();
                    final boolean equals = newExpression.getIntegerValue() != null
                            ? newExpression.getIntegerValue().equals(caseExpression.getIntegerValue())
                            : newExpression.toString().equals(caseExpression.toString());
                    if (equals)
                        throw new ParserException(newExpression, "Choice '" + getName() +
                                "' has duplicated case!");
                }
                allExpressions.add(newCaseExpression.getExpression());
            }
        }
    }

    private void checkEnumerationCases()
    {
        final ZserioType selectorExpressionType = selectorExpression.getExprZserioType();
        if (selectorExpressionType instanceof EnumType)
        {
            final EnumType resolvedEnumType = (EnumType)TypeReference.resolveType(selectorExpressionType);
            final List<EnumItem> availableEnumItems = resolvedEnumType.getItems();
            final List<EnumItem> unhandledEnumItems = new ArrayList<EnumItem>(resolvedEnumType.getItems());

            for (ChoiceCase choiceCase : choiceCases)
            {
                final List<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
                for (ChoiceCaseExpression caseExpression : caseExpressions)
                {
                    final Expression expression = caseExpression.getExpression();
                    final Set<EnumItem> referencedEnumItems =
                            expression.getReferencedSymbolObjects(EnumItem.class);
                    for (EnumItem referencedEnumItem : referencedEnumItems)
                    {
                        if (!availableEnumItems.contains(referencedEnumItem))
                            throw new ParserException(expression, "Choice '" + getName() +
                                    "' has case with different enumeration type than selector!");
                        unhandledEnumItems.remove(referencedEnumItem);
                    }
                }
            }

            if (choiceDefault == null)
            {
                for (EnumItem unhandledEnumItem : unhandledEnumItems)
                {
                    ZserioToolPrinter.printWarning(this, "Enumeration value '" +
                            unhandledEnumItem.getName() + "' is not handled in choice '" + getName() +
                            "'.");
                }
            }
        }
    }

    private final Expression selectorExpression;
    private final List<ChoiceCase> choiceCases;
    private final ChoiceDefault choiceDefault;

    private boolean isChoiceDefaultUnreachable = false;
}
