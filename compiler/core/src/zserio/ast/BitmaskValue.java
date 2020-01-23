package zserio.ast;
import java.math.BigInteger;

/**
 * AST node for values defined by bitmask types.
 */
public class BitmaskValue extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location        AST node location.
     * @param name            Name of the bitmask value.
     * @param valueExpression Expression value of the bitmask.
     * @param docComment      Documentation comment belonging to this node.
     */
    public BitmaskValue(AstLocation location, String name, Expression valueExpression, DocComment docComment)
    {
        super(location, docComment);

        this.name = name;
        this.valueExpression = valueExpression;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitBitmaskValue(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        if (valueExpression != null)
            valueExpression.accept(visitor);
    }

    /**
     * Gets the name of the bitmask value.
     *
     * @return Returns name of the value.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets value expression.
     *
     * @return Value expression or null if value expression has not been specified.
     */
    public Expression getValueExpression()
    {
        return valueExpression;
    }

    /**
     * Gets the integer value.
     *
     */
    public BigInteger getValue()
    {
        return value;
    }

    /**
     * Sets the default integer value.
     *
     * This method is called only if value is not defined in the language.
     *
     * @param value Integer value.
     */
    void setValue(BigInteger value)
    {
        this.value = value;
    }

    /**
     * Evaluates bitmask value expression.
     *
     * This method can be called from Expression.evaluate() method if some expression refers to bitmask value
     * before definition of this item. Therefore 'isEvaluated' check is necessary.
     */
    void evaluate()
    {
        if (!isEvaluated)
        {
            if (valueExpression != null)
            {
                if (valueExpression.getExprType() != Expression.ExpressionType.INTEGER)
                    throw new ParserException(valueExpression, "Bitmask value '" + getName() +
                            "' is not an integral type!");

                value = valueExpression.getIntegerValue();
                if (value.compareTo(BigInteger.ZERO) == -1)
                    throw new ParserException(valueExpression, "Bitmask value '" + getName() +
                            "(" + value + ") cannot be negative!");
            }

            isEvaluated = true;
        }
    }

    private final String name;
    private final Expression valueExpression;

    private BigInteger value = null;
    private boolean isEvaluated = false;
}