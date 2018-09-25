package zserio.ast;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * AST node for array types.
 *
 * Array types are Zserio types as well.
 */
public class ArrayType extends TokenAST implements ZserioType
{
    @Override
    public Package getPackage()
    {
        return elementType.getPackage();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Iterable<ZserioType> getUsedTypeList()
    {
        throw new InternalError("ArrayType.getUsedTypeList() is not implemented!");
    }

    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitArrayType(this);
    }

    /**
     * Gets unresolved Zserio type of this array element.
     *
     * @return Unresolved Zserio type of this array element.
     */
    public ZserioType getElementType()
    {
        return elementType;
    }

    /**
     * Gets expression which represents array length.
     *
     * @return Array length expression or null if this array is implicit.
     */
    public Expression getLengthExpression()
    {
        return lengthExpression;
    }

    /**
     * Checks if the array is implicit.
     *
     * @return true if the array is implicit, otherwise false.
     */
    public boolean isImplicit()
    {
        return isImplicit;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        if (child instanceof ZserioType)
        {
            if (elementType != null)
                return false;
            elementType = (ZserioType)child;
        }
        else if (child instanceof Expression)
        {
            if (lengthExpression != null)
                return false;
            lengthExpression = (Expression)child;
        }
        else if (child.getType() == ZserioParserTokenTypes.IMPLICIT)
        {
            isImplicit = true;
        }
        else
        {
            return false;
        }

        return true;
    }

    @Override
    protected void check() throws ParserException
    {
        // fill member which depend on element type
        name = elementType.getName() + "[]";

        // check length expression
        if (lengthExpression != null)
        {
            if (isImplicit)
                throw new ParserException(lengthExpression,
                        "Length expression is not allowed for implicit arrays!");

            if (lengthExpression.getExprType() != Expression.ExpressionType.INTEGER)
                throw new ParserException(lengthExpression,
                        "Invalid length expression for array. Length must be integer!");
        }
    }

    private static final long serialVersionUID = 6231540349926054424L;

    private ZserioType elementType = null;
    private Expression lengthExpression = null;
    private boolean isImplicit = false;

    private String name;
}
