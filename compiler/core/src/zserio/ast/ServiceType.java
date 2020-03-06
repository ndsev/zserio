package zserio.ast;

import java.util.Collections;
import java.util.List;

/**
 * AST node for service types.
 *
 * Service types are Zserio types as well.
 */
public class ServiceType extends DocumentableAstNode implements ZserioScopedType
{
    /**
     * Constructor.
     *
     * @param location   AST node location.
     * @param pkg        Package to which belongs the service type.
     * @param name       Name of the service type.
     * @param methods    List of all methods which belong to the service type.
     * @param docComment Documentation comment belonging to this node.
     */
    public ServiceType(AstLocation location, Package pkg, String name, List<ServiceMethod> methods,
            DocComment docComment)
    {
        super(location, docComment);

        this.pkg = pkg;
        this.name = name;
        this.methods = methods;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitServiceType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        for (ServiceMethod method : methods)
            method.accept(visitor);
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
     * Gets list of service's methods.
     *
     * @return List of methods.
     */
    public List<ServiceMethod> getMethodList()
    {
        return Collections.unmodifiableList(methods);
    }

    private final Scope scope = new Scope(this);

    private final Package pkg;
    private final String name;
    private final List<ServiceMethod> methods;
}
