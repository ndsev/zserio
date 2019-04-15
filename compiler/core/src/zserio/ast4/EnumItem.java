package zserio.ast4;

import java.math.BigInteger;

import org.antlr.v4.runtime.Token;

public class EnumItem extends AstNodeBase
{
    public EnumItem(Token locationToken, String name, Expression valueExpression)
    {
        super(locationToken);

        this.name = name;
        this.valueExpression = valueExpression;
    }

    @Override
    public void accept(ZserioVisitor visitor)
    {
        visitor.visitEnumItem(this);
    }

    @Override
    public void visitChildren(ZserioVisitor visitor)
    {
        if (valueExpression != null)
            valueExpression.accept(visitor);
    }

    /**
     * Evaluates enumeration item value expression.
     *
     * @param defaultValue Enumeration item value to use if value expression has not been specified.
     *
     * @throws ParserException Throws in case of any error during evaluation.
     */
    public void evaluateValueExpression(BigInteger defaultValue) throws ParserException
    {
        if (valueExpression != null)
        {
            // there is a value for this enumeration item => evaluate and check value expression
            /* TODO
            valueExpression.evaluateTree();

            if (valueExpression.getExprType() != Expression.ExpressionType.INTEGER)
                throw new ParserException(valueExpression, "Enumeration item '" + getName() +
                        "' has non-integer value!");
            value = valueExpression.getIntegerValue(); */
        }
        else
        {
            value = defaultValue;
        }
    }

    /**
     * Gets the name of enumeration item.
     *
     * @return Returns name of enumeration item.
     */
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
     * Gets the enumeration type which is owner of the enumeration item.
     *
     * @return Returns enumeration type which is owner of the enumeration item.
     */
    public EnumType getEnumType()
    {
        return enumType;
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
     * Gets documentation comment associated to this enumeration item.
     *
     * @return Documentation comment token associated to this enumeration item.
     */
    /* TODO
    public DocCommentToken getDocComment()
    {
        return tokenWithDoc.getHiddenDocComment();
    }*/

    /**
     * Sets the enumeration type which is owner of the enumeration item.
     *
     * @param enumType Owner to set.
     */
    protected void setEnumType(EnumType enumType)
    {
        this.enumType = enumType;
    }

    private final String name;
    private final Expression valueExpression;

    private EnumType enumType = null;
    private BigInteger value = null;
}
