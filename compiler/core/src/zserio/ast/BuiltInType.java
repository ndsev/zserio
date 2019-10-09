package zserio.ast;

/**
 * AST abstract node for all built-in types.
 *
 * This is an abstract class for all built-in Zserio types (boolean, float16, string, ...).
 */
public abstract class BuiltInType extends AstNodeBase implements ZserioType
{
    /**
     * Constructor from AST node location and the name.
     *
     * @param location AST node location.
     * @param name     Name of the AST node taken from grammar.
     */
    public BuiltInType(AstLocation location, String name)
    {
        super(location);
        this.name = name;
    }

    @Override
    public Package getPackage()
    {
        // built-in types do not have any package
        throw new InternalError("BuiltInType.getPackage() is not implemented!");
    }

    @Override
    public String getName()
    {
        return name;
    }

    private final String name;
}
