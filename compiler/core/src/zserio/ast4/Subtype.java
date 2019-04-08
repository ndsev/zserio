package zserio.ast4;

import org.antlr.v4.runtime.Token;

public class Subtype extends AstNodeBase implements ZserioType
{
    public Subtype(Token token, String name, ZserioType targetType)
    {
        super(token);
        this.name = name;
        this.targetType = targetType;
    }

    @Override
    public Package getPackage()
    {
        return null; // TODO:
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Iterable<ZserioType> getUsedTypeList()
    {
        return null; // TODO:
    }

    @Override
    public void walk(ZserioListener listener)
    {
        listener.beginSubtype(this);
        targetType.walk(listener);
        listener.endSubtype(this);
    }

    private final String name;
    private final ZserioType targetType;
}