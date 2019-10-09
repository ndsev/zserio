package zserio.ast;

/**
 * AST node for Boolean types.
 *
 * Boolean types are Zserio types as well.
 */
public class BooleanType extends BuiltInType implements FixedSizeType
{
    /**
     * Constructor from AST node location and the name.
     *
     * @param location AST node location.
     * @param name     Name of the AST node taken from grammar.
     */
    public BooleanType(AstLocation location, String name)
    {
        super(location, name);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitBooleanType(this);
    }

    @Override
    public int getBitSize()
    {
        return 1;
    }
}
