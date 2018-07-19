package zserio.ast;

/**
 * AST node for string types.
 *
 * String types (Zserio command 'string') are Zserio types as well.
 */
public class StringType extends BuiltInType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitStringType(this);
    }

    private static final long serialVersionUID = -602092333356845654L;
}
