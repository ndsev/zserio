package zserio.ast4;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import zserio.tools.HashUtil;

/**
 * AST node for service types.
 *
 * Service types are Zserio types as well.
 */
public class ServiceType extends AstNodeBase implements ZserioScopedType, Comparable<ServiceType>
{
    public ServiceType(Token token, Package pkg, String name, List<Rpc> rpcs)
    {
        super(token);

        this.pkg = pkg;
        this.name = name;
        this.rpcs = rpcs;

        for (Rpc rpc : rpcs)
            rpc.setServiceType(this);
    }

    @Override
    public void accept(ZserioVisitor visitor)
    {
        visitor.visitServiceType(this);
    }

    @Override
    public void visitChildren(ZserioVisitor visitor)
    {
        for (Rpc rpc : rpcs)
            rpc.accept(visitor);
    }

    @Override
    public int compareTo(ServiceType other)
    {
        final int result = getName().compareTo(other.getName());
        if (result != 0)
            return result;

        return getPackage().getPackageName().compareTo(other.getPackage().getPackageName());
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;

        if (other instanceof ServiceType)
            return compareTo((ServiceType)other) == 0;

        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, getName());
        hash = HashUtil.hash(hash, getPackage().getPackageName());

        return hash;
    }

    @Override
    public Package getPackage()
    {
        return pkg;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Scope getScope()
    {
        return scope;
    }

    /**
     * Gets rpc methods list.
     *
     * @return List of RPC methods.
     */
    public Iterable<Rpc> getRpcList()
    {
        return rpcs;
    }

    private final Scope scope = new Scope(this);
    private final Package pkg;

    private final String name;
    private final List<Rpc> rpcs;
}
