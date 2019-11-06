package zserio.ast;

import java.math.BigInteger;
import java.util.List;

/**
 * AST node for compound fields.
 *
 * This field is used by all Compound types (structure types, choice types, ...).
 */
public class Field extends DocumentableAstNode
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
     * @param docComment         Documentation comment belonging to this node.
     */
    public Field(AstLocation location, TypeInstantiation typeInstantiation, String name, boolean isAutoOptional,
            Expression alignmentExpr, Expression offsetExpr,Expression initializerExpr,
            Expression optionalClauseExpr, Expression constraintExpr, DocComment docComment)
    {
        this(location, typeInstantiation, name, isAutoOptional, alignmentExpr, offsetExpr, initializerExpr,
                optionalClauseExpr, constraintExpr, false, null, docComment);
    }

    /**
     * Constructor from Choice and Union types.
     *
     * @param location               AST node location.
     * @param fieldTypeInstantiation Field type instantiation.
     * @param name                   Field name.
     * @param constraintExpr         Constraint expression or null if it's not defined.
     * @param docComment             Documentation comment belonging to this node.
     */
    public Field(AstLocation location, TypeInstantiation fieldTypeInstantiation, String name,
            Expression constraintExpr, DocComment docComment)
    {
        this(location, fieldTypeInstantiation, name, false, null, null, null, null, constraintExpr, false, null,
                docComment);
    }

    /**
     * Constructor from SQL Table types.
     *
     * @param location               AST node location.
     * @param fieldTypeInstantiation Field type instantiation.
     * @param name                   Field name.
     * @param isVirtual              True if field is virtual.
     * @param sqlConstraint          SQL constraint or null if it's not defined.
     */
    public Field(AstLocation location, TypeInstantiation fieldTypeInstantiation, String name, boolean isVirtual,
            SqlConstraint sqlConstraint, DocComment docComment)
    {
        this(location, fieldTypeInstantiation, name, false, null, null, null, null, null, isVirtual,
                sqlConstraint, docComment);
    }

    /**
     * Constructor from SQL Database types.
     *
     * @param location               AST node location.
     * @param fieldTypeInstantiation Field type instantiation.
     * @param name                   Field name.
     */
    public Field(AstLocation location, TypeInstantiation fieldTypeInstantiation, String name,
            DocComment docComment)
    {
        this(location, fieldTypeInstantiation, name, false, null, null, null, null, null, false, null,
                docComment);
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
     * Gets the name of the field.
     *
     * @return Returns name of the field.
     */
    public String getName()
    {
        return name;
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
     * Even if SQL constraint is not specified in Zserio, this method returns valid SQL constraint which
     * corresponds to Zserio SQL constraint default behavior (valid only for SQL tables).
     *
     * @return SQL constraint or null if the owner is not SQL table.
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
     * Checks the compound field.
     */
    void check(Package pkg)
    {
        // check offset expression type
        if (offsetExpr != null)
        {
            final ZserioType exprZserioType = offsetExpr.getExprZserioType();
            if (!(exprZserioType instanceof IntegerType) || ((IntegerType)exprZserioType).isSigned())
                throw new ParserException(offsetExpr, "Offset expression for field '" + getName() +
                        "' is not an unsigned integer type!");
        }

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
            final ZserioType fieldBaseType =
                    typeInstantiation.getTypeReference().getBaseTypeReference().getType();
            ExpressionUtil.checkExpressionType(initializerExpr, fieldBaseType);

            // check if expression requires owner context (contains field, parameter or function)
            if (initializerExpr.requiresOwnerContext())
                throw new ParserException(initializerExpr, "Initializer must be a constant expression!");

            // check integer initializer range
            ExpressionUtil.checkIntegerExpressionRange(initializerExpr, fieldBaseType, name);
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

        // check field name
        if (pkg.getVisibleType(this, PackageName.EMPTY, getName()) != null)
            throw new ParserException(this, "'" + getName() + "' is a defined type in this package!");
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
                isVirtual, instantiatedSqlConstraint, getDocComment());
    }

    private Field(AstLocation location, TypeInstantiation typeInstantiation, String name,
            boolean isAutoOptional, Expression alignmentExpr, Expression offsetExpr, Expression initializerExpr,
            Expression optionalClauseExpr, Expression constraintExpr, boolean isVirtual,
            SqlConstraint sqlConstraint, DocComment docComment)
    {
        super(location, docComment);

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
}
