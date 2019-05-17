package zserio.ast;

import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;


/**
 * AST node for compound fields.
 *
 * This field is used by all Compound types (structure types, choice types, ...).
 */
public class Field extends AstNodeWithDoc
{
    /**
     * Constructor from Structure types.
     *
     * @param token              ANTLR4 token to localize AST node in the sources.
     * @param fieldType          Field type.
     * @param name               Field name.
     * @param isAutoOptional     Auto optional flag.
     * @param alignmentExpr      Alignment expression or null if it's not defined.
     * @param offsetExpr         Offset expression or null if it's not defined.
     * @param initializerExpr    Initializer expression or null if it's not defined.
     * @param optionalClauseExpr Optional clause expression or null if it's not defined.
     * @param constraintExpr     Constraint expression or null if it's not defined.
     * @param docComment         Documentation comment belonging to this node.
     */
    public Field(Token token, ZserioType fieldType, String name, boolean isAutoOptional,
            Expression alignmentExpr, Expression offsetExpr, Expression initializerExpr,
            Expression optionalClauseExpr, Expression constraintExpr, DocComment docComment)
    {
        this(token, fieldType, name, isAutoOptional, alignmentExpr, offsetExpr, initializerExpr,
                optionalClauseExpr, constraintExpr, false, null, docComment);
    }

    /**
     * Constructor from Choice and Union types.
     *
     * @param token              ANTLR4 token to localize AST node in the sources.
     * @param fieldType          Field type.
     * @param name               Field name.
     * @param constraintExpr     Constraint expression or null if it's not defined.
     * @param docComment         Documentation comment belonging to this node.
     */
    public Field(Token token, ZserioType fieldType, String name, Expression constraintExpr,
            DocComment docComment)
    {
        this(token, fieldType, name, false, null, null, null, null, constraintExpr, false, null, docComment);
    }

    /**
     * Constructor from SQL Table types.
     *
     * @param token         ANTLR4 token to localize AST node in the sources.
     * @param fieldType     Field type.
     * @param name          Field name.
     * @param isVirtual     True if field is virtual.
     * @param sqlConstraint SQL constraint or null if it's not defined.
     */
    public Field(Token token, ZserioType fieldType, String name, boolean isVirtual, SqlConstraint sqlConstraint,
            DocComment docComment)
    {
        this(token, fieldType, name, false, null, null, null, null, null, isVirtual, sqlConstraint, docComment);
    }

    /**
     * Constructor from SQL Database types.
     *
     * @param token     ANTLR4 token to localize AST node in the sources.
     * @param fieldType Field type.
     * @param name      Field name.
     */
    public Field(Token token, ZserioType fieldType, String name, DocComment docComment)
    {
        this(token, fieldType, name, false, null, null, null, null, null, false, null, docComment);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitField(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        fieldType.accept(visitor);
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

        super.visitChildren(visitor);
    }

    /**
     * Gets Zserio type associated with the field.
     *
     * @return Zserio type associated with the field as has been specified in Zserio.
     */
    public ZserioType getFieldType()
    {
        return fieldType;
    }

    /**
     * Gets referenced Zserio type associated with the field.
     *
     * In case that the field is an array, this methods gets the element type.
     * If the type is a an instantiation, it returns it's referenced type.
     *
     * Result is either a built-in type or a type reference.
     *
     * @return Referenced Zserio type associated with the field.
     */
    public ZserioType getFieldReferencedType()
    {
        ZserioType referencedType = fieldType;

        // get array element type
        if (fieldType instanceof ArrayType)
            referencedType = ((ArrayType)referencedType).getElementType();

        // get instantiated type
        if (referencedType instanceof TypeInstantiation)
            referencedType = ((TypeInstantiation)referencedType).getReferencedType();

        return referencedType;
    }

    /**
     * Gets a list of parameters used in the field.
     *
     * This method is useful only for fields of type "type instantiation" as no other fields can have
     * parameters.
     *
     * The order of the returned items is the order of the parameters.
     *
     * @return A list of parameters or empty list if a field has no parameters.
     */
    public List<TypeInstantiation.InstantiatedParameter> getInstantiatedParameters()
    {
        final ZserioType resolvedFieldType = TypeReference.resolveType(fieldType);
        if (!(resolvedFieldType instanceof TypeInstantiation))
            return Collections.unmodifiableList(new LinkedList<TypeInstantiation.InstantiatedParameter>());

        return ((TypeInstantiation)resolvedFieldType).getInstantiatedParameters();
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
     * @return true if the field is has been defined using "optional" keyword in Zserio or if the field
     *         has optional clause.
     */
    public boolean getIsOptional()
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
        // check initializer expression type
        if (initializerExpr != null)
        {
            // check expression type
            final ZserioType fieldBaseType = TypeReference.resolveBaseType(fieldType);
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

        // check field name
        if (pkg.getVisibleType(this, PackageName.EMPTY, getName()) != null)
            throw new ParserException(this, "'" + getName() + "' is a defined type in this package!");
    }

    private Field(Token token, ZserioType fieldType, String name, boolean isAutoOptional,
            Expression alignmentExpr, Expression offsetExpr, Expression initializerExpr,
            Expression optionalClauseExpr, Expression constraintExpr, boolean isVirtual,
            SqlConstraint sqlConstraint, DocComment docComment)
    {
        super(token, docComment);

        this.fieldType = fieldType;
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

    private final ZserioType fieldType;
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
