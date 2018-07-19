package zserio.ast;

/**
 * AST node for float types.
 *
 * Float types are Zserio types as well.
 */
public class FloatType extends BuiltInType implements FixedSizeType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitFloatType(this);
    }

    @Override
    public int getBitSize()
    {
        return 16;
    }

    private static final long serialVersionUID = 125193189598509024L;
}
