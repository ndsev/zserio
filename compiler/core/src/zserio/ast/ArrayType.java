package zserio.ast;

import java.util.List;

/**
 * AST node for array types.
 *
 * Array types are Zserio types as well.
 */
public class ArrayType extends BuiltInType
{
    /**
     * Array type
     *
     * @param location                 AST node location.
     * @param elementTypeInstantiation Instantiation of the element type.
     * @param isImplicit               Whether this is an implicit array.
     * @param lengthExpression         Length expression.
     */
    public ArrayType(AstLocation location, TypeInstantiation elementTypeInstantiation, boolean isImplicit,
            Expression lengthExpression)
    {
        super(location, "[]");

        this.elementTypeInstantiation = elementTypeInstantiation;
        this.isImplicit = isImplicit;
        this.lengthExpression = lengthExpression;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitArrayType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        elementTypeInstantiation.accept(visitor);
        if (lengthExpression != null)
            lengthExpression.accept(visitor);
    }

    /**
     * Gets instantiation of the element type.
     *
     * @return Type instantiation.
     */
    public TypeInstantiation getElementTypeInstantiation()
    {
        return elementTypeInstantiation;
    }

    /**
     * Gets whether the array is an implicit array.
     *
     * \note Implicit arrays have no length expression.
     *
     * @return True if the array is implicit, false otherwise.
     */
    public boolean isImplicit()
    {
        return isImplicit;
    }

    /**
     * Gets length expression.
     *
     * @return Array length expression or null when this is an implicit or auto array.
     */
    public Expression getLengthExpression()
    {
        return lengthExpression;
    }

    /**
     * Checks the array type.
     */
    void check()
    {
        // check length expression
        if (lengthExpression != null)
        {
            if (lengthExpression.getExprType() != Expression.ExpressionType.INTEGER)
            {
                throw new ParserException(lengthExpression,
                        "Invalid length expression for array. Length must be integer!");
            }
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
    ArrayType instantiate(List<TemplateParameter> templateParameters, List<TemplateArgument> templateArguments)
    {
        final TypeInstantiation instantiatedElementTypeInstantiation =
                elementTypeInstantiation.instantiate(templateParameters, templateArguments);
        final Expression instantiatedLengthExpression = getLengthExpression() == null ? null :
                getLengthExpression().instantiate(templateParameters, templateArguments);
        return new ArrayType(getLocation(), instantiatedElementTypeInstantiation, isImplicit,
                instantiatedLengthExpression);
    }

    final TypeInstantiation elementTypeInstantiation;
    final boolean isImplicit;
    final Expression lengthExpression;
}
