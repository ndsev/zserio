package zserio.ast;

import java.math.BigInteger;
import java.util.List;

/**
 * AST node for items defined by enumeration types.
 */
public final class EnumItem extends DocumentableAstNode implements ScopeSymbol
{
    /**
     * Constructor.
     *
     * @param location        AST node location.
     * @param name            Name of the enumeration item.
     * @param valueExpression Expression value of the enumeration item.
     * @param isDeprecated    Flag whether this enum item is marked as deprecated.
     * @param isRemoved       Flag whether this enum item is marked as removed.
     * @param docComments     List of documentation comments belonging to this node.
     */
    public EnumItem(AstLocation location, String name, Expression valueExpression, boolean isDeprecated,
            boolean isRemoved, List<DocComment> docComments)
    {
        super(location, docComments);

        this.name = name;
        this.valueExpression = valueExpression;
        this.isDeprecated = isDeprecated;
        this.isRemoved = isRemoved;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitEnumItem(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        if (valueExpression != null)
            valueExpression.accept(visitor);
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Gets enumeration item value expression.
     *
     * @return Enumeration item value expression or null if value expression has not been specified.
     */
    public Expression getValueExpression()
    {
        return valueExpression;
    }

    /**
     * Gets the integer value which represents the enumeration item.
     *
     * @return Returns the integer value of the enumeration item.
     */
    public BigInteger getValue()
    {
        return value;
    }

    /**
     * Gets whether this enumeration item is deprecated.
     *
     * @return Returns true when this item is deprecated, false otherwise.
     */
    public boolean isDeprecated()
    {
        return isDeprecated;
    }

    /**
     * Gets whether this enumeration item is removed.
     *
     * @return Returns true when this item is removed, false otherwise.
     */
    public boolean isRemoved()
    {
        return isRemoved;
    }

    /**
     * Sets the default integer value which represents the enumeration item.
     *
     * This method is called only if enumeration item value is not defined in the language.
     *
     * @param value Default integer value of the enumeration item.
     */
    void setValue(BigInteger value)
    {
        this.value = value;
    }

    /**
     * Evaluates enumeration item value expression.
     *
     * This method can be called from Expression.evaluate() method if some expression refers to enumeration
     * item before definition of this item. Therefore 'isEvaluated' check is necessary.
     */
    void evaluate()
    {
        if (!isEvaluated)
        {
            if (valueExpression != null)
            {
                if (valueExpression.getExprType() != Expression.ExpressionType.INTEGER)
                    throw new ParserException(
                            valueExpression, "Enumeration item '" + getName() + "' has non-integer value!");
                value = valueExpression.getIntegerValue();
            }

            isEvaluated = true;
        }
    }

    private final String name;
    private final Expression valueExpression;
    private final boolean isDeprecated;
    private final boolean isRemoved;

    private BigInteger value = null;
    private boolean isEvaluated = false;
}
