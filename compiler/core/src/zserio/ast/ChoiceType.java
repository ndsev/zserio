package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import zserio.tools.WarningsConfig;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for Choice types.
 *
 * Choice types are Zserio types as well.
 */
public final class ChoiceType extends CompoundType
{
    /**
     * Constructor.
     *
     * @param location              AST node location.
     * @param pkg                   Package to which belongs the choice type.
     * @param name                  Name of the choice type.
     * @param templateParameters    List of template parameters.
     * @param typeParameters        List of parameters for the choice type.
     * @param selectorExpression    Selector expression of the choice type.
     * @param choiceCases           List of all choice cases.
     * @param choiceDefault         Choice default case or null if default case is not defined.
     * @param functions             List of all functions of the choice type.
     * @param docComments           List of documentation comments belonging to this node.
     */
    public ChoiceType(AstLocation location, Package pkg, String name,
            List<TemplateParameter> templateParameters, List<Parameter> typeParameters,
            Expression selectorExpression, List<ChoiceCase> choiceCases, ChoiceDefault choiceDefault,
            List<Function> functions, List<DocComment> docComments)
    {
        super(location, pkg, name, templateParameters, typeParameters,
                getChoiceFields(choiceCases, choiceDefault), functions, docComments);

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
        for (DocComment docComment : getDocComments())
            docComment.accept(visitor);

        for (TemplateParameter templateParameter : getTemplateParameters())
            templateParameter.accept(visitor);

        for (Parameter parameter : getTypeParameters())
            parameter.accept(visitor);

        selectorExpression.accept(visitor);

        for (ChoiceCase choiceCase : choiceCases)
            choiceCase.accept(visitor);

        if (choiceDefault != null)
            choiceDefault.accept(visitor);

        for (Function function : getFunctions())
            function.accept(visitor);
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
    ChoiceType instantiateImpl(List<TemplateArgument> templateArguments, Package instantiationPackage)
    {
        final List<Parameter> instantiatedTypeParameters = new ArrayList<Parameter>();
        for (Parameter typeParameter : getTypeParameters())
        {
            instantiatedTypeParameters.add(
                    typeParameter.instantiate(getTemplateParameters(), templateArguments));
        }

        final Expression instantiatedSelectorExpression =
                getSelectorExpression().instantiate(getTemplateParameters(), templateArguments);

        final List<ChoiceCase> instantiatedChoiceCases = new ArrayList<ChoiceCase>();
        for (ChoiceCase choiceCase: getChoiceCases())
            instantiatedChoiceCases.add(choiceCase.instantiate(getTemplateParameters(), templateArguments));

        final ChoiceDefault instantiatedChoiceDefault = getChoiceDefault() == null ? null :
            getChoiceDefault().instantiate(getTemplateParameters(), templateArguments);

        final List<Function> instantiatedFunctions = new ArrayList<Function>();
        for (Function function : getFunctions())
            instantiatedFunctions.add(function.instantiate(getTemplateParameters(), templateArguments));

        return new ChoiceType(getLocation(), instantiationPackage, getName(),
                new ArrayList<TemplateParameter>(), instantiatedTypeParameters, instantiatedSelectorExpression,
                instantiatedChoiceCases, instantiatedChoiceDefault, instantiatedFunctions, getDocComments());
    }

    /**
     * Checks the choice type.
     *
     * @param warningsConfig Warnings subsystem configuration.
     */
    void check(WarningsConfig warningsConfig)
    {
        super.check();

        checkSymbolNames();
        checkSqlTableFields();

        checkExtendedFields();

        isChoiceDefaultUnreachable = checkUnreachableDefault();
        checkSelectorType();
        checkCaseTypes();
        checkEnumerationCases(warningsConfig);
        checkBitmaskCases();
        checkDuplicatedCases();

        checkOptionalReferencesInSelector(warningsConfig);
    }

    private void checkExtendedFields()
    {
        for (Field field : getFields())
        {
            // nested fields cannot contain extended fields
            if (containsExtendedField(field))
            {
                final ParserStackedException stackedException = new ParserStackedException(
                        field.getLocation(), "Field '" + field.getName() + "' contains an extended field!");
                trackExtendedField(field, stackedException);
                throw stackedException;
            }
        }
    }

    @Override
    protected boolean hasBranchWithoutImplicitArray()
    {
        // at least one field must have branch without implicit array, or at least one case must lead to empty
        // choice, or it must be empty choice
        for (ChoiceCase choiceCase : choiceCases)
        {
            if (choiceCase.getField() == null || hasFieldBranchWithoutImplicitArray(choiceCase.getField()))
                return true;
        }

        if (choiceDefault != null)
        {
            if (choiceDefault.getField() == null ||
                    hasFieldBranchWithoutImplicitArray(choiceDefault.getField()))
            {
                return true;
            }
        }

        return getFields().isEmpty();
    }

    @Override
    protected boolean hasEmptyBranch(boolean implicitCanBeEmpty)
    {
        // at least one field must have empty branch, or at least one case must lead to empty choice,
        // or it must be empty choice
        for (ChoiceCase choiceCase : choiceCases)
        {
            if (choiceCase.getField() == null || hasFieldEmptyBranch(choiceCase.getField(), implicitCanBeEmpty))
                return true;
        }

        if (choiceDefault != null)
        {
            if (choiceDefault.getField() == null ||
                    hasFieldEmptyBranch(choiceDefault.getField(), implicitCanBeEmpty))
                return true;
        }

        return getFields().isEmpty();
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
            selectorExpressionType != Expression.ExpressionType.ENUM &&
            selectorExpressionType != Expression.ExpressionType.BITMASK)
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
                            : ExpressionComparator.equals(newExpression, caseExpression);
                    if (equals)
                    {
                        final ParserStackedException stackedException = new ParserStackedException(
                                newExpression.getLocation(), "Choice '" + getName() + "' has duplicated case!");
                        stackedException.pushMessage(caseExpression.getLocation(), "    First handled here");
                        throw stackedException;
                    }
                }
                allExpressions.add(newCaseExpression.getExpression());
            }
        }
    }

    private void checkEnumerationCases(WarningsConfig warningsConfig)
    {
        final ZserioType selectorExpressionType = selectorExpression.getExprZserioType();
        if (selectorExpressionType instanceof EnumType)
        {
            final EnumType resolvedEnumType = (EnumType)selectorExpressionType;

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
                    ZserioToolPrinter.printWarning(this, "Enumeration item '" +
                            unhandledEnumItem.getName() + "' is not handled in choice '" + getName() + "'.",
                            warningsConfig, WarningsConfig.CHOICE_UNHANDLED_ENUM_ITEM);
                }
            }
        }
    }

    private void checkBitmaskCases()
    {
        final ZserioType selectorExpressionType = selectorExpression.getExprZserioType();
        if (selectorExpressionType instanceof BitmaskType)
        {
            final BitmaskType resolvedBitmaskType = (BitmaskType)selectorExpressionType;

            final List<BitmaskValue> availableBitmaskValues = resolvedBitmaskType.getValues();

            for (ChoiceCase choiceCase : choiceCases)
            {
                final List<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
                for (ChoiceCaseExpression caseExpression : caseExpressions)
                {
                    final Expression expression = caseExpression.getExpression();
                    final Set<BitmaskValue> referencedBitmaskValues =
                            expression.getReferencedSymbolObjects(BitmaskValue.class);
                    for (BitmaskValue referencedBitmaskValue : referencedBitmaskValues)
                    {
                        if (!availableBitmaskValues.contains(referencedBitmaskValue))
                            throw new ParserException(expression, "Choice '" + getName() +
                                    "' has case with different bitmask type than selector!");
                    }
                }
            }
        }
    }

    private void checkOptionalReferencesInSelector(WarningsConfig warningsConfig)
    {
        // in case of ternary operator, we are not able to check correctness => such warning should be
        // enabled explicitly by command line
        if (!selectorExpression.containsTernaryOperator())
        {
            final Set<Field> referencedFields = selectorExpression.getReferencedOptionalFields().keySet();
            for (Field referencedField : referencedFields)
            {
                ZserioToolPrinter.printWarning(selectorExpression, "Choice '" + getName() + "' selector " +
                        "contains reference to optional field '" + referencedField.getName() + "'.",
                        warningsConfig, WarningsConfig.OPTIONAL_FIELD_REFERENCE);
            }
        }
    }

    private final Expression selectorExpression;
    private final List<ChoiceCase> choiceCases;
    private final ChoiceDefault choiceDefault;

    private boolean isChoiceDefaultUnreachable = false;
}
