package zserio.ast4;

import org.antlr.v4.runtime.Token;

/**
 * AST node for array types.
 *
 * Array types are Zserio types as well.
 */
public class ArrayType extends AstNodeBase implements ZserioType
{
    public ArrayType(Token token, ZserioType elementType, Expression lengthExpression,
            boolean isImplicit)
    {
        super(token);

        this.elementType = elementType;
        this.lengthExpression = lengthExpression;
        this.isImplicit = isImplicit;
    }

    @Override
    public void walk(ZserioListener listener)
    {
        listener.beginArrayType(this);

        elementType.walk(listener);
        lengthExpression.walk(listener);

        listener.endArrayType(this);
    }

    @Override
    public Package getPackage()
    {
        return elementBaseType.getPackage();
    }

    @Override
    public String getName()
    {
        return elementBaseType.getName() + "[]";
    }

    /*@Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitArrayType(this);
    }*/

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

    /*@Override
    protected void check() throws ParserException
    {
        // resolve element base type
        elementBaseType = TypeReference.resolveBaseType(elementType);

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
    }*/ // TODO:

    private final ZserioType elementType;
    private final ZserioType elementBaseType = null;
    private final Expression lengthExpression;
    private final boolean isImplicit;
}
