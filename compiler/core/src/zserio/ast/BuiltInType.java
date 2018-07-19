package zserio.ast;

import zserio.tools.PackageManager;

/**
 * AST abstract node for all built-in types.
 *
 * This is an abstract class for all built-in Zserio types (boolean, float16, string, ...).
 */
public abstract class BuiltInType extends TokenAST implements ZserioType
{
    /**
     * Default constructor.
     */
    protected BuiltInType()
    {
        ZserioTypeContainer.add(this);
    }

    @Override
    public Package getPackage()
    {
        return PackageManager.get().builtInPackage;
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
