package zserio.ast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.ast.doc.DocCommentToken;

/**
 * AST node for enumeration items.
 */
public class EnumItem extends TokenAST
{
    /**
     * Evaluates enumeration item value.
     *
     * @param defaultValue Enumeration item value to use if value expression has not been specified.
     *
     * @throws ParserException Throws in case of any error during evaluation.
     */
    public void evaluateValue(BigInteger defaultValue) throws ParserException
    {
        if (valueExpression != null)
        {
            // there is a value for this enumeration item => evaluate and check value expression
            valueExpression.evaluateTree();

            value = valueExpression.getIntegerValue();
            if (value == null)
                throw new ParserException(valueExpression, "Enumeration item '" + getName() +
                        "' has non-integer value!");
        }
        else
        {
            value = defaultValue;
        }
    }

    /**
     * Adds expression which used the enumeration item.
     *
     * This method is called directly from AST node Expression during evaluation.
     *
     * @param expression Expression which uses the enumeration item.
     */
    public void addUsedByExpression(Expression expression)
    {
        usedByExpressionList.add(expression);
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
     * Gets list of expressions which use the enumeration item.
     *
     * This method is used by documentation emitter.
     *
     * @return List of expressions which use the enumeration item.
     */
    public Iterable<Expression> getUsedByExpressionList()
    {
        return usedByExpressionList;
    }

    /**
     * Gets documentation comment associated to this enumeration item.
     *
     * @return Documentation comment token associated to this enumeration item.
     */
    public DocCommentToken getDocComment()
    {
        return tokenWithDoc.getHiddenDocComment();
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        if (name == null)
        {
            if (!(child instanceof TokenAST))
                return false;
            tokenWithDoc = (TokenAST)child;
            tokenWithDoc.evaluateHiddenDocComment(enumType);
            name = child.getText();
        }
        else
        {
            if (!(child instanceof Expression))
                return false;
            valueExpression = (Expression)child;
        }

        return true;
    }

    /**
     * Sets the enumeration type which is owner of the enumeration item.
     *
     * @param enumType Owner to set.
     */
    protected void setEnumType(EnumType enumType)
    {
        this.enumType = enumType;
    }

    private static final long serialVersionUID = -2577973642614324740L;

    private String name = null;
    private Expression valueExpression = null;

    private EnumType enumType;
    private BigInteger value;

    // TODO the problem is in doc emitter which tries to get owner as Choice type from these expressions.
    // don't store this to HashSet because the same expressions can be in different scopes!
    private final List<Expression> usedByExpressionList = new ArrayList<Expression>();
    private TokenAST tokenWithDoc;
}
