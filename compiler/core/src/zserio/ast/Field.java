package zserio.ast;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.tools.WarningsConfig;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for compound fields.
 *
 * This field is used by all Compound types (structure types, choice types, ...).
 */
public class Field extends DocumentableAstNode implements ScopeSymbol
{
    /**
     * Constructor from Structure types.
     *
     * @param location           AST node location.
     * @param isExtended         Whether the field is defined using the 'extend' keyword.
     * @param typeInstantiation  Field type instantiation.
     * @param name               Field name.
     * @param isAutoOptional     Auto optional flag.
     * @param alignmentExpr      Alignment expression or null if it's not defined.
     * @param offsetExpr         Offset expression or null if it's not defined.
     * @param initializerExpr    Initializer expression or null if it's not defined.
     * @param optionalClauseExpr Optional clause expression or null if it's not defined.
     * @param constraintExpr     Constraint expression or null if it's not defined.
     * @param docComments        List of documentation comments belonging to this node.
     */
    public Field(AstLocation location, boolean isExtended, TypeInstantiation typeInstantiation, String name,
            boolean isAutoOptional, Expression alignmentExpr, Expression offsetExpr, Expression initializerExpr,
            Expression optionalClauseExpr, Expression constraintExpr, List<DocComment> docComments)
    {
        this(location, isExtended, typeInstantiation, name, isAutoOptional, alignmentExpr, offsetExpr,
                initializerExpr, optionalClauseExpr, constraintExpr, false, null, docComments);
    }

    /**
     * Constructor from Choice and Union types.
     *
     * @param location               AST node location.
     * @param fieldTypeInstantiation Field type instantiation.
     * @param name                   Field name.
     * @param constraintExpr         Constraint expression or null if it's not defined.
     * @param docComments            List of documentation comments belonging to this node.
     */
    public Field(AstLocation location, TypeInstantiation fieldTypeInstantiation,
            String name, Expression constraintExpr, List<DocComment> docComments)
    {
        this(location, false, fieldTypeInstantiation, name, false, null, null, null, null, constraintExpr,
                false, null, docComments);
    }

    /**
     * Constructor from SQL Table types.
     *
     * @param location               AST node location.
     * @param fieldTypeInstantiation Field type instantiation.
     * @param name                   Field name.
     * @param isVirtual              True if field is virtual.
     * @param sqlConstraint          SQL constraint or null if it's not defined.
     * @param docComments            List of documentation comments belonging to this node.
     */
    public Field(AstLocation location, TypeInstantiation fieldTypeInstantiation, String name, boolean isVirtual,
            SqlConstraint sqlConstraint, List<DocComment> docComments)
    {
        this(location, false, fieldTypeInstantiation, name, false, null, null, null, null, null, isVirtual,
                sqlConstraint, docComments);
    }

    /**
     * Constructor from SQL Database types.
     *
     * @param location               AST node location.
     * @param fieldTypeInstantiation Field type instantiation.
     * @param name                   Field name.
     * @param docComments            List of documentation comments belonging to this node.
     */
    public Field(AstLocation location, TypeInstantiation fieldTypeInstantiation, String name,
            List<DocComment> docComments)
    {
        this(location, false, fieldTypeInstantiation, name, false, null, null, null, null, null, false, null,
                docComments);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitField(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        typeInstantiation.accept(visitor);
        if (alignmentExpr != null)
            alignmentExpr.accept(visitor);
        if (offsetExpr != null)
            offsetExpr.accept(visitor);
        if (initializerExpr != null)
            initializerExpr.accept(visitor);
        if (optionalClauseExpr != null)
            optionalClauseExpr.accept(visitor);
        if (constraintExpr != null)
            constraintExpr.accept(visitor);
        if (sqlConstraint != null)
            sqlConstraint.accept(visitor);
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Gets flag which indicates if the field has been defined using "extend" keyword in Zserio.
     *
     * @return True when the field is extended.
     */
    public boolean isExtended()
    {
        return isExtended;
    }

    /**
     * Gets the field's type instantiation.
     *
     * @return Type instantiation.
     */
    public TypeInstantiation getTypeInstantiation()
    {
        return typeInstantiation;
    }

    /**
     * Gets alignment expression associated with the field.
     *
     * @return Alignment expression or null if no alignment expression has been specified.
     */
    public Expression getAlignmentExpr()
    {
        return alignmentExpr;
    }

    /**
     * Gets offset expression associated with the field.
     *
     * @return Offset expression or null if no offset expression has been specified.
     */
    public Expression getOffsetExpr()
    {
        return offsetExpr;
    }

    /**
     * Gets initializer expression associated with the field.
     *
     * @return Initializer expression or null if no initializer expression has been specified.
     */
    public Expression getInitializerExpr()
    {
        return initializerExpr;
    }

    /**
     * Gets optional clause expression associated with the field.
     *
     * @return Optional clause expression or null if no optional clause expression has been specified.
     */
    public Expression getOptionalClauseExpr()
    {
        return optionalClauseExpr;
    }

    /**
     * Gets constraint expression associated with the field.
     *
     * @return Constraint expression or null if no constraint expression has been specified.
     */
    public Expression getConstraintExpr()
    {
        return constraintExpr;
    }

    /**
     * Gets SQL constraint associated with the field.
     *
     * @return SQL constraint or null if no SQL constraint has been specified.
     */
    public SqlConstraint getSqlConstraint()
    {
        return sqlConstraint;
    }

    /**
     * Gets flag which indicates if the field has been defined using "sql_virtual" keyword in Zserio.
     *
     * @return true if the field is virtual column in SQL table.
     */
    public boolean isVirtual()
    {
        return isVirtual;
    }

    /**
     * Gets flag which indicates if the field is optional.
     *
     * @return True if the field has been defined using "optional" keyword in Zserio or if the field
     *         has optional clause.
     */
    public boolean isOptional()
    {
        return isAutoOptional || optionalClauseExpr != null;
    }

    /**
     * Gets flag which indicates if the field is is packable.
     *
     * Currently this is true for integral fields which fulfill other requirements to be packable and also
     * for all compounds, which however can have no packable fields at all.
     *
     * @return true if the field is packable.
     */
    public boolean isPackable()
    {
        return isPackable;
    }

    /**
     * Evaluates the compound field.
     *
     * This method calculates and sets isUnpackable flag.
     */
    void evaluate()
    {
        ZserioType fieldBaseType = typeInstantiation.getBaseType();
        if (typeInstantiation instanceof ArrayInstantiation)
        {
            final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
            if (arrayInstantiation.isImplicit())
                isPackable = false;
            fieldBaseType = arrayInstantiation.getElementTypeInstantiation().getBaseType();
        }

        if (!(fieldBaseType instanceof CompoundType) && !ArrayInstantiation.isSimpleTypePackable(fieldBaseType))
            isPackable = false;

        if (offsetExpr != null)
        {
            // the offset must be evaluated to the field (single or array) => mark it as not packable
            final Set<Field> referencedFieldObjects = offsetExpr.getReferencedSymbolObjects(Field.class);
            if (!referencedFieldObjects.isEmpty())
            {
                final Field referencedField =  referencedFieldObjects.iterator().next();
                final TypeInstantiation referencedFieldInst = referencedField.getTypeInstantiation();
                if (referencedFieldInst instanceof ArrayInstantiation &&
                        ((ArrayInstantiation)referencedFieldInst).isPacked())
                {
                    throw new ParserException(offsetExpr, "Packed array cannot be used as offset array!");
                }

                referencedField.isPackable = false;
            }
        }
    }

    /**
     * Checks the compound field.
     */
    void check(Package pkg, WarningsConfig warningsConfig)
    {
        // check offset expression type
        checkOffsetExpression();

        // check alignment expression type
        if (alignmentExpr != null)
        {
            final BigInteger alignmentValue = alignmentExpr.getIntegerValue();
            if (alignmentValue == null || alignmentValue.compareTo(BigInteger.ZERO) < 0)
                throw new ParserException(alignmentExpr, "Alignment expression for field '" + getName() +
                        "' is not positive integer!");
            if (alignmentValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0)
                throw new ParserException(alignmentExpr, "Alignment expression for field '" + getName() +
                        "' is bigger than integer max value (" + Integer.MAX_VALUE + ")!");
        }

        // check initializer expression type
        if (initializerExpr != null)
        {
            // check expression type
            ExpressionUtil.checkExpressionType(initializerExpr, typeInstantiation);

            // check if expression requires owner context (contains field, parameter or function)
            if (initializerExpr.requiresOwnerContext())
                throw new ParserException(initializerExpr, "Initializer must be a constant expression!");

            // check integer initializer range
            ExpressionUtil.checkIntegerExpressionRange(initializerExpr, typeInstantiation, name);
        }

        // check optional expression type
        if (optionalClauseExpr != null)
        {
            if (optionalClauseExpr.getExprType() != Expression.ExpressionType.BOOLEAN)
                throw new ParserException(optionalClauseExpr, "Optional expression for field '" +
                        getName() + "' is not boolean!");
        }

        // check constraint expression type
        if (constraintExpr != null)
        {
            if (constraintExpr.getExprType() != Expression.ExpressionType.BOOLEAN)
                throw new ParserException(constraintExpr, "Constraint expression for field '" +
                        getName() + "' is not boolean!");
        }

        // check optional references (should be at the end to check correct expression types at first)
        checkOptionalReferences(warningsConfig);
    }

    /**
     * Instantiate the field.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments Template arguments.
     *
     * @return New field instantiated from this using the given template arguments.
     */
    Field instantiate(List<TemplateParameter> templateParameters, List<TemplateArgument> templateArguments)
    {
        final TypeInstantiation instantiatedTypeInstantiation =
                typeInstantiation.instantiate(templateParameters, templateArguments);

        final Expression instantiatedAlignmentExpr = getAlignmentExpr() == null ? null :
                getAlignmentExpr().instantiate(templateParameters, templateArguments);
        final Expression instantiatedOffsetExpr = getOffsetExpr() == null ? null :
                getOffsetExpr().instantiate(templateParameters, templateArguments);
        final Expression instantiatedInitializerExpr = getInitializerExpr() == null ? null :
                getInitializerExpr().instantiate(templateParameters, templateArguments);
        final Expression instantiatedOptionalClauseExpr = getOptionalClauseExpr() == null ? null :
                getOptionalClauseExpr().instantiate(templateParameters, templateArguments);
        final Expression instantiatedConstraintExpr = getConstraintExpr() == null ? null :
                getConstraintExpr().instantiate(templateParameters, templateArguments);

        final SqlConstraint instantiatedSqlConstraint = getSqlConstraint() == null ? null :
                getSqlConstraint().instantiate(templateParameters, templateArguments);

        return new Field(getLocation(), isExtended, instantiatedTypeInstantiation, name,
                isAutoOptional, instantiatedAlignmentExpr, instantiatedOffsetExpr, instantiatedInitializerExpr,
                instantiatedOptionalClauseExpr, instantiatedConstraintExpr,
                isVirtual, instantiatedSqlConstraint, getDocComments());
    }

    private void checkOffsetExpression()
    {
        if (offsetExpr != null)
        {
            final ZserioType exprZserioType = offsetExpr.getExprZserioType();
            if (!(exprZserioType instanceof IntegerType && !((IntegerType)exprZserioType).isSigned() &&
                    exprZserioType instanceof FixedSizeType))
            {
                throw new ParserException(offsetExpr, "Offset expression for field '" + getName() +
                        "' is not an unsigned fixed sized integer type!");
            }

            if (offsetExpr.containsFunctionCallOutOfArray())
                throw new ParserException(offsetExpr, "Function call cannot be used for offset setting!");

            if (offsetExpr.containsTernaryOperatorOutOfArray())
                throw new ParserException(offsetExpr, "Ternary operator cannot be used for offset setting!");

            if (offsetExpr.op2() == null)
            {
                final AstNode symbolObject = offsetExpr.getExprSymbolObject();
                if (symbolObject instanceof Parameter)
                {
                    final Parameter parameter = (Parameter)symbolObject;
                    if (parameter.getTypeReference().getBaseTypeReference().getType() instanceof BuiltInType)
                    {
                        throw new ParserException(offsetExpr, "Built-in type parameter '" +
                                parameter.getName() + "' cannot be used as an offset!");
                    }
                }
                else if (symbolObject instanceof Constant)
                {
                    throw new ParserException(offsetExpr, "Constant '" + ((Constant)symbolObject).getName() +
                            "' cannot be used as an offset!");
                }
            }
        }
    }

    private void checkOptionalReferences(WarningsConfig warningsConfig)
    {
        checkOptionalReferencesInOffset(warningsConfig);
        checkOptionalReferencesInTypeArguments(warningsConfig);
        checkOptionalReferencesInOptionalClause(warningsConfig);
        checkOptionalReferencesInConstraint(warningsConfig);
        checkOptionalReferencesInArrayLength(warningsConfig);
        checkOptionalReferencesInBitfieldLength(warningsConfig);
    }

    private void checkOptionalReferencesInOffset(WarningsConfig warningsConfig)
    {
        if (offsetExpr != null)
            checkOptionalReferencesInExpression(offsetExpr, "offset", warningsConfig);
    }

    private void checkOptionalReferencesInTypeArguments(WarningsConfig warningsConfig)
    {
        TypeInstantiation fieldTypeInstantiation = typeInstantiation;
        if (fieldTypeInstantiation instanceof ArrayInstantiation)
            fieldTypeInstantiation = ((ArrayInstantiation)fieldTypeInstantiation).getElementTypeInstantiation();

        if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
        {
            final Iterable<InstantiatedParameter> instantiatedParameters =
                    ((ParameterizedTypeInstantiation)fieldTypeInstantiation).getInstantiatedParameters();
            for (InstantiatedParameter instantiatedParameter : instantiatedParameters)
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                checkOptionalReferencesInExpression(argumentExpression, "type arguments", warningsConfig);
            }
        }
    }

    private void checkOptionalReferencesInOptionalClause(WarningsConfig warningsConfig)
    {
        if (optionalClauseExpr != null)
        {
            // in case of ternary operator, we are not able to check correctness => such warning should be
            // enabled explicitly by command line
            if (!optionalClauseExpr.containsTernaryOperator())
            {
                final Map<Field, Expression.OptionalFieldInfo> referencedOptionalFields =
                        optionalClauseExpr.getReferencedOptionalFields();
                for (Map.Entry<Field, Expression.OptionalFieldInfo> referencedOptionalEntry :
                        referencedOptionalFields.entrySet())
                {
                    final Field referencedOptionalField = referencedOptionalEntry.getKey();
                    final Expression referencedOptionalClauseExpr = referencedOptionalField.optionalClauseExpr;
                    if (referencedOptionalClauseExpr == null)
                    {
                        ZserioToolPrinter.printWarning(this, "Field '" + name + "' contains reference to " +
                                "auto optional field '" + referencedOptionalField.getName() +
                                "' in optional clause.",
                                warningsConfig, WarningsConfig.OPTIONAL_FIELD_REFERENCE);
                    }
                    else
                    {
                        // check expressions using comparison formatter
                        final List<Expression> leftAndExprs =
                                referencedOptionalEntry.getValue().getLeftAndExprs();
                        boolean found = false;
                        for (Expression leftAndExpr : leftAndExprs)
                        {
                            if (ExpressionComparator.equals(leftAndExpr, referencedOptionalClauseExpr))
                            {
                                found = true;
                                break;
                            }
                        }
                        if (!found)
                        {
                            ZserioToolPrinter.printWarning(this, "Field '" + name + "' does not have left " +
                                    "'and' condition of optional field '"+ referencedOptionalField.getName() +
                                    "' referenced in optional clause.",
                                    warningsConfig, WarningsConfig.OPTIONAL_FIELD_REFERENCE);
                        }
                    }
                }
            }
        }
    }

    private void checkOptionalReferencesInConstraint(WarningsConfig warningsConfig)
    {
        if (constraintExpr != null)
            checkOptionalReferencesInExpression(constraintExpr, "constraint", warningsConfig);
    }

    private void checkOptionalReferencesInArrayLength(WarningsConfig warningsConfig)
    {
        if (typeInstantiation instanceof ArrayInstantiation)
        {
            final Expression lengthExpr = ((ArrayInstantiation)typeInstantiation).getLengthExpression();
            if (lengthExpr != null)
                checkOptionalReferencesInExpression(lengthExpr, "array length", warningsConfig);
        }
    }

    private void checkOptionalReferencesInBitfieldLength(WarningsConfig warningsConfig)
    {
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            final Expression lengthExpr =
                    ((DynamicBitFieldInstantiation)typeInstantiation).getLengthExpression();
            checkOptionalReferencesInExpression(lengthExpr, "dynamic bitfield length", warningsConfig);
        }
    }

    private void checkOptionalReferencesInExpression(Expression expr, String exprName,
            WarningsConfig warningsConfig)
    {
        // in case of ternary operator, we are not able to check correctness => such warning should be
        // enabled explicitly by command line
        if (!expr.containsTernaryOperator())
        {
            final Map<Field, Expression.OptionalFieldInfo> referencedOptionalFields =
                    expr.getReferencedOptionalFields();

            for (Map.Entry<Field, Expression.OptionalFieldInfo> referencedOptionalEntry :
                    referencedOptionalFields.entrySet())
            {
                final Field referencedOptionalField = referencedOptionalEntry.getKey();
                final List<AstNode> referencedDotPrefix = referencedOptionalEntry.getValue().getDotPrefix();
                if (haveFieldsDifferentOptional(referencedOptionalField, referencedDotPrefix))
                {
                    if (optionalClauseExpr == null)
                    {
                        ZserioToolPrinter.printWarning(this, "Field '" + name + "' is not optional " +
                                "and contains reference to optional field '" +
                                referencedOptionalField.getName() + "' in " + exprName + ".",
                                warningsConfig, WarningsConfig.OPTIONAL_FIELD_REFERENCE);
                    }
                    else
                    {
                        ZserioToolPrinter.printWarning(this, "Field '" + name + "' has different optional " +
                                "condition than field '"+ referencedOptionalField.getName() +
                                "' referenced in " + exprName + ".",
                                warningsConfig, WarningsConfig.OPTIONAL_FIELD_REFERENCE);
                    }
                }
            }
        }
    }

    private boolean haveFieldsDifferentOptional(Field referencedOptionalField,
            List<AstNode> referencedDotPrefix)
    {
        // check references to itself (could happen for constraints for example)
        if (equals(referencedOptionalField) && referencedDotPrefix.isEmpty())
            return false;

        // check expressions using comparison formatter
        if (optionalClauseExpr != null && referencedOptionalField.optionalClauseExpr != null &&
                ExpressionComparator.equals(optionalClauseExpr, referencedOptionalField.optionalClauseExpr,
                        referencedDotPrefix))
            return false;

        return true;
    }

    private Field(AstLocation location, boolean isExtended, TypeInstantiation typeInstantiation, String name,
            boolean isAutoOptional, Expression alignmentExpr, Expression offsetExpr, Expression initializerExpr,
            Expression optionalClauseExpr, Expression constraintExpr, boolean isVirtual,
            SqlConstraint sqlConstraint, List<DocComment> docComments)
    {
        super(location, docComments);

        this.isExtended = isExtended;

        this.typeInstantiation = typeInstantiation;
        this.name = name;
        this.isAutoOptional = isAutoOptional;

        this.alignmentExpr = alignmentExpr;
        this.offsetExpr = offsetExpr;
        this.initializerExpr = initializerExpr;
        this.optionalClauseExpr = optionalClauseExpr;
        this.constraintExpr = constraintExpr;

        this.isVirtual = isVirtual;
        this.sqlConstraint = sqlConstraint;

        this.isPackable = true;
    }

    private final boolean isExtended;

    private final TypeInstantiation typeInstantiation;
    private final String name;
    private final boolean isAutoOptional;

    private final Expression offsetExpr;
    private final Expression alignmentExpr;
    private final Expression initializerExpr;
    private final Expression optionalClauseExpr;
    private final Expression constraintExpr;

    private final boolean isVirtual;
    private final SqlConstraint sqlConstraint;

    private boolean isPackable;
}
