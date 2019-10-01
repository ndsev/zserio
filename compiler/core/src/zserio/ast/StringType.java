package zserio.ast;

/**
 * AST node for String types.
 *
 * String types (Zserio keyword 'string') are Zserio types as well.
 */
public class StringType extends BuiltInType
{
    /**
     * Constructor from AST node location and the name.
     *
     * @param location AST node location.
     * @param name     Name of the AST node taken from grammar.
     */
    public StringType(AstLocation location, String name)
    {
        super(location, name);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitStringType(this);
    }
}
