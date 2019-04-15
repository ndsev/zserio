package zserio.ast4;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * AST node for fields.
 */
public class Field extends AstNodeBase
{
    public Field(Token token, ZserioType fieldType, String name, boolean isAutoOptional,
            Expression alignmentExpr, Expression offsetExpr, Expression initializerExpr,
            Expression optionalClauseExpr, Expression constraintExpr)
    {
        super(token);


        this.fieldType = fieldType;
        this.name = name;
        this.isAutoOptional = isAutoOptional;

        this.alignmentExpr = alignmentExpr;
        this.offsetExpr = offsetExpr;
        this.initializerExpr = initializerExpr;
        this.optionalClauseExpr = optionalClauseExpr;
        this.constraintExpr = constraintExpr;

        this.isVirtual = false;
        this.sqlConstraint = null;
    }

    // table field
    public Field(Token token, ZserioType fieldType, String name, boolean isVirtual, SqlConstraint sqlConstraint)
    {
        super(token);

        this.fieldType = fieldType;
        this.name = name;
        this.isAutoOptional = false;

        this.alignmentExpr = null;
        this.offsetExpr = null;
        this.initializerExpr = null;
        this.optionalClauseExpr = null;
        this.constraintExpr = null;

        this.isVirtual = isVirtual;
        this.sqlConstraint = sqlConstraint; // TODO: use SqlConstraint.createDefaultFieldConstraint(); if null?
    }

    // database field
    public Field(Token token, ZserioType fieldType, String name)
    {
        super(token);

        this.fieldType = fieldType;
        this.name = name;
        this.isAutoOptional = false;

        this.alignmentExpr = null;
        this.offsetExpr = null;
        this.initializerExpr = null;
        this.optionalClauseExpr = null;
        this.constraintExpr = null;

        this.isVirtual = false;
        this.sqlConstraint = null;
    }

    @Override
    public void accept(ZserioVisitor visitor)
    {
        visitor.visitField(this);
    }

    @Override
    public void visitChildren(ZserioVisitor visitor)
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
            return new LinkedList<TypeInstantiation.InstantiatedParameter>();

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
     * corresponds to Zserio SQL constraint default behavior.
     *
     * @return SQL constraint.
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
     * Gets the list of Zserio types referenced by all expressions of the field.
     *
     * @param clazz Zserio type to use.
     *
     * @return List of referenced Zserio types of given type.
     */
    /*public <T extends ZserioType> Set<T> getReferencedTypes(Class<? extends T> clazz)
    {
        final Set<T> referencedTypes = new HashSet<T>();

        // check direct expressions
        if (optionalClauseExpr != null)
            referencedTypes.addAll(optionalClauseExpr.getReferencedSymbolObjects(clazz));
        if (constraintExpr != null)
            referencedTypes.addAll(constraintExpr.getReferencedSymbolObjects(clazz));
        if (offsetExpr != null)
            referencedTypes.addAll(offsetExpr.getReferencedSymbolObjects(clazz));
        if (alignmentExpr != null)
            referencedTypes.addAll(alignmentExpr.getReferencedSymbolObjects(clazz));

        ZserioType checkedType = fieldType;
        if (checkedType instanceof ArrayType)
        {
            // check array fields which have expressions as well
            final ArrayType arrayType = (ArrayType)checkedType;
            final Expression lengthExpression = arrayType.getLengthExpression();
            if (lengthExpression != null)
                referencedTypes.addAll(lengthExpression.getReferencedSymbolObjects(clazz));

            checkedType = arrayType.getElementType();
        }

        if (checkedType instanceof TypeInstantiation)
        {
            // check type instantiations which have expressions in arguments
            final TypeInstantiation typeInstantiation = (TypeInstantiation)checkedType;
            for (TypeInstantiation.InstantiatedParameter instantiatedParameter :
                    typeInstantiation.getInstantiatedParameters())
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                referencedTypes.addAll(argumentExpression.getReferencedSymbolObjects(clazz));
            }
        }
        else if (checkedType instanceof BitFieldType)
        {
            // check bit fields which have expressions as well
            final Expression lengthExpression = ((BitFieldType)checkedType).getLengthExpression();
            referencedTypes.addAll(lengthExpression.getReferencedSymbolObjects(clazz));
        }

        return referencedTypes;
    }*/ //TODO:

    /**
     * Gets documentation comment associated to this field.
     *
     * @return Documentation comment token associated to this field.
     */
    /*public DocCommentToken getDocComment()
    {
        return tokenWithDoc != null ? tokenWithDoc.getHiddenDocComment() : null;
    }*/ // TODO:

    /*@Override
    protected void check() throws ParserException
    {
        // check field name
        if (compoundType.getPackage().getVisibleType(this, PackageName.EMPTY, getName()) != null)
            throw new ParserException(this, "'" + getName() + "' is a defined type in this package!");

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

            if (isAutoOptional)
                throw new ParserException(optionalClauseExpr, "Auto optional field '" + getName() +
                        "' cannot contain if clause!");
        }

        // check constraint expression type
        if (constraintExpr != null && constraintExpr.getExprType() != Expression.ExpressionType.BOOLEAN)
            throw new ParserException(constraintExpr, "Constraint expression for field '" +
                    getName() + "' is not boolean!");

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
    }*/ // TODO:

    /**
     * Sets the compound type which is owner of the field.
     *
     * @param compoundType Owner to set.
     */
    protected void setCompoundType(CompoundType compoundType)
    {
        this.compoundType = compoundType;

        if (sqlConstraint != null)
            sqlConstraint.setCompoundType(compoundType);
    }

    /**
     * Evaluates hidden doc comment within the AST subtree of the given comment.
     *
     * Traverses only left most path in the AST subtree (i.e. only first children) and takes the last found
     * comment. All other comments are marked as not used.
     * \note Note that "priority" of comments is given by order of children in the Field token,
     *       which is given in the parser grammar.
     *
     * @param token Token to be searched for documentation comments.
     * @throws ParserException In case of invalid AST token.
     */
    /*private void evaluateDocComment(TokenAST token) throws ParserException
    {
        TokenAST currentToken = token;

        while (currentToken != null)
        {
            if (currentToken.evaluateHiddenDocComment(compoundType))
            {
                if (tokenWithDoc != null)
                    tokenWithDoc.setDocCommentNotUsed();
                tokenWithDoc = currentToken;
            }

            currentToken = (TokenAST)currentToken.getFirstChild();
        }
    }*/

    private CompoundType compoundType = null;
    private final ZserioType fieldType;
    private final String name;
    private final boolean isAutoOptional;
    private final Expression offsetExpr;
    private final Expression alignmentExpr;
    private final Expression initializerExpr;
    private final Expression optionalClauseExpr;
    private final Expression constraintExpr;

    private final SqlConstraint sqlConstraint;
    private final boolean isVirtual;
}
