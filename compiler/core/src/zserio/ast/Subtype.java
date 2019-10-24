package zserio.ast;

import zserio.ast.ZserioType;

/**
 * AST node for Subtypes.
 *
 * Subtypes are Zserio types as well.
 */
public class Subtype extends DocumentableAstNode implements ZserioType
{
    /**
     * @param location            AST node location.
     * @param pkg                 Package to which belongs the subtype.
     * @param targetTypeReference Type reference to the target type.
     * @param name                Name of the subtype.
     * @param docComment          Documentation comment belonging to this node.
     */
    public Subtype(AstLocation location, Package pkg, TypeReference targetTypeReference,
            String name, DocComment docComment)
    {
        super(location, docComment);

        this.pkg = pkg;
        this.typeReference = targetTypeReference;
        this.name = name;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitSubtype(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        typeReference.accept(visitor);
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

    /**
     * Gets the target type reference.
     *
     * @return Type reference to the target type.
     */
    public TypeReference getTypeReference()
    {
        return typeReference;
    }

    /**
     * Gets reference to the base type.
     *
     * @return Reference to base type of the target type.
     */
    public TypeReference getBaseTypeReference()
    {
        return baseTypeReference;
    }

    /**
     * Resolves the subtype to a defined type called at the end of linking phase.
     *
     * @return Resolved reference to base type of this subtype.
     */
    TypeReference resolve()
    {
        if (resolvingState == ResolvingState.RESOLVED)
            return baseTypeReference;

        // detect cycles in subtype definitions
        if (resolvingState == ResolvingState.RESOLVING)
            throw new ParserException(this, "Cyclic dependency detected in subtype '" +
                    getName() + "' definition!");

        resolvingState = ResolvingState.RESOLVING;

        typeReference.resolve(false); // make sure the type reference is resolved
        if (typeReference.getType() instanceof Subtype)
            baseTypeReference = ((Subtype)typeReference.getType()).resolve();
        else
            baseTypeReference = typeReference;

        resolvingState = ResolvingState.RESOLVED;

        return baseTypeReference;
    }

    private enum ResolvingState
    {
        UNRESOLVED,
        RESOLVING,
        RESOLVED
    };

    private final Package pkg;
    private final TypeReference typeReference;
    private final String name;

    private ResolvingState resolvingState = ResolvingState.UNRESOLVED;
    private TypeReference baseTypeReference = null;
}
