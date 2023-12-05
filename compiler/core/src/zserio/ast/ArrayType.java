package zserio.ast;

/**
 * AST node for array types.
 *
 * Array types are Zserio types as well.
 */
public final class ArrayType extends BuiltInType
{
    /**
     * Array type
     *
     * @param location AST node location.
     */
    public ArrayType(AstLocation location)
    {
        super(location, "[]");
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitArrayType(this);
    }
}
