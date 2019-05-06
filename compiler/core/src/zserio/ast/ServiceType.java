package zserio.ast;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.Token;

import zserio.tools.HashUtil;

/**
 * AST node for service types.
 *
 * Service types are Zserio types as well.
 */
public class ServiceType extends AstNodeWithDoc implements ZserioScopedType, Comparable<ServiceType>
{
    /**
     * Constructor.
     *
     * @param token      ANTLR4 token to localize AST node in the sources.
     * @param pkg        Package to which belongs the service type.
     * @param name       Name of the service type.
     * @param rpcs       List of all RPCs which belong to the service type.
     * @param docComment Documentation comment belonging to this node.
     */
    public ServiceType(Token token, Package pkg, String name, List<Rpc> rpcs, DocComment docComment)
    {
        super(token, docComment);

        this.pkg = pkg;
        this.name = name;
        this.rpcs = rpcs;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitServiceType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (Rpc rpc : rpcs)
            rpc.accept(visitor);

        super.visitChildren(visitor);
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
    public List<Rpc> getRpcList()
    {
        return Collections.unmodifiableList(rpcs);
    }

    private final Scope scope = new Scope(this);

    private final Package pkg;
    private final String name;
    private final List<Rpc> rpcs;
}
