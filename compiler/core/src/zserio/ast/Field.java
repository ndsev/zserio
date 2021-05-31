package zserio.ast;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

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
    public Field(AstLocation location, TypeInstantiation typeInstantiation, String name, boolean isAutoOptional,
            Expression alignmentExpr, Expression offsetExpr, Expression initializerExpr,
            Expression optionalClauseExpr, Expression constraintExpr, List<DocComment> docComments)
    {
        this(location, typeInstantiation, name, isAutoOptional, alignmentExpr, offsetExpr,
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
    public Field(AstLocation location, TypeInstantiation fieldTypeInstantiation, String name,
            Expression constraintExpr, List<DocComment> docComments)
    {
        this(location, fieldTypeInstantiation, name, false, null, null, null, null, constraintExpr,
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
        this(location, fieldTypeInstantiation, name, false, null, null, null, null, null, isVirtual,
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
        this(location, fieldTypeInstantiation, name, false, null, null, null, null, null, false, null,
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
     * Gets the field's type instantiation.
     *
     * @return Type instantiation.
     */
    public TypeInstantiation getTypeInstantiation()
    {
        return typeInstantiation;
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
    public boolean getIsVirtual()
    {
        return isVirtual;
    }

    /**
     * Gets flag which indicates if the field is packable.
     *
     * Currently, all fields are packable except of fields which are used as offsets.
     *
     * @return true if the field is packable.
     */
    public boolean getIsPackable()
    {
        return isPackable;
    }

    /**
     * Checks the compound field.
     */
    void check(Package pkg)
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

        return new Field(getLocation(), instantiatedTypeInstantiation, name, isAutoOptional,
                instantiatedAlignmentExpr, instantiatedOffsetExpr, instantiatedInitializerExpr,
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

            if (offsetExpr.containsFunctionCall())
                throw new ParserException(offsetExpr, "Function call cannot be used in offset expression!");

            if (offsetExpr.getExprSymbolObject() instanceof Field)
            {
                // the offset has been evaluated to the single field => mark it as not packable
                final Field offsetField = (Field) offsetExpr.getExprSymbolObject();
                offsetField.isPackable = false;
            }
            else
            {
                // the offset has been evaluated to the array => find the last evaluated array instantiation
                final Set<Field> referencedFieldObjects = offsetExpr.getReferencedSymbolObjects(Field.class);
                ArrayInstantiation lastReferencedArrayInst = null;
                for (Field referencedFieldObject : referencedFieldObjects)
                {
                    final TypeInstantiation referencedTypeInst = referencedFieldObject.getTypeInstantiation();
                    if (referencedTypeInst instanceof ArrayInstantiation)
                        lastReferencedArrayInst = (ArrayInstantiation)referencedTypeInst;
                }
                if (lastReferencedArrayInst != null && lastReferencedArrayInst.isPacked())
                {
                    throw new ParserException(offsetExpr,
                            "Packed array cannot be used as indexed offset array!");
                }
            }

            for (Field referencedFieldObject : offsetExpr.getReferencedSymbolObjects(Field.class))
                System.out.println("referencedFieldObject:  " + referencedFieldObject.getName());
            System.out.println("ExprType = " + offsetExpr.getExprType());
            System.out.println("ExprZserioType = " + offsetExpr.getExprZserioType());
            System.out.println("ExprSymbolObject = " + offsetExpr.getExprSymbolObject());

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

    private Field(AstLocation location, TypeInstantiation typeInstantiation, String name,
            boolean isAutoOptional, Expression alignmentExpr, Expression offsetExpr, Expression initializerExpr,
            Expression optionalClauseExpr, Expression constraintExpr, boolean isVirtual,
            SqlConstraint sqlConstraint, List<DocComment> docComments)
    {
        super(location, docComments);

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
