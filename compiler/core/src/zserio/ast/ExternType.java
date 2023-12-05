package zserio.ast;

/**
 * AST node for Extern types.
 *
 * Extern types (Zserio keyword 'extern') are Zserio types as well.
 */
public final class ExternType extends BuiltInType
{
    /**
     * Constructor from AST node location and the name.
     *
     * @param location AST node location.
     * @param name     Name of the AST node taken from grammar.
     */
    public ExternType(AstLocation location, String name)
    {
        super(location, name);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitExternType(this);
    }
}
