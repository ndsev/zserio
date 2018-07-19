package zserio.ast;

/**
 * AST node for boolean types.
 *
 * Boolean types are Zserio types as well.
 */
public class BooleanType extends BuiltInType implements FixedSizeType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitBooleanType(this);
    }

    @Override
    public int getBitSize()
    {
        return 1;
    }

    private static final long serialVersionUID = 2528776617383794636L;
}
