package zserio.ast;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;

/**
 * AST node for Array types.
 *
 * Array types are Zserio types as well.
 */
public class ArrayType extends AstNodeBase implements ZserioType
{
    /**
     * Constructor.
     *
     * @param location         ANTLR4 token to localize AST node in the sources.
     * @param elementType      Zserio type of the array element.
     * @param lengthExpression Length expression associated to the array.
     * @param isImplicit       True for implicit arrays.
     */
    public ArrayType(Token token, ZserioType elementType, Expression lengthExpression,
            boolean isImplicit)
    {
        super(token);

        this.elementType = elementType;
        this.lengthExpression = lengthExpression;
        this.isImplicit = isImplicit;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitArrayType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        elementType.accept(visitor);
        if (lengthExpression != null)
            lengthExpression.accept(visitor);
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

    /**
     * Evaluates the array type.
     */
    void evaluate()
    {
        // resolve element base type
        elementBaseType = TypeReference.resolveBaseType(elementType);
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
                throw new ParserException(lengthExpression,
                        "Invalid length expression for array. Length must be integer!");
        }
    }

    private final ZserioType elementType;
    private final Expression lengthExpression;
    private final boolean isImplicit;

    private ZserioType elementBaseType = null;
}
