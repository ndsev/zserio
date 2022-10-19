package zserio.ast;

/**
 * AST node for bytes type.
 *
 * Bytes types are Zserio types as well.
 */
public class BytesType extends BuiltInType
{
    /**
     * Constructor from AST node location and the name.
     *
     * @param location AST node location.
     * @param name     Name of the AST node taken from grammar.
     */
    public BytesType(AstLocation location, String name)
    {
        super(location, name);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitBytesType(this);
    }
};