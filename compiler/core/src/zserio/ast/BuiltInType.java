package zserio.ast;

/**
 * AST abstract node for all built-in types.
 *
 * This is an abstract class for all built-in Zserio types (boolean, float16, string, ...).
 */
public abstract class BuiltInType extends TokenAST implements ZserioType
{
    @Override
    public Package getPackage()
    {
        // built-in types do not have any package
        throw new InternalError("BuiltInType.getPackage() is not implemented!");
    }

    @Override
    public String getName()
    {
        return getText();
    }

    @Override
    public Iterable<ZserioType> getUsedTypeList()
    {
        throw new InternalError("BuiltInType.getUsedTypeList() is not implemented!");
    }

    private static final long serialVersionUID = -602092333356843634L;
}
