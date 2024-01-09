package zserio.ast;

import java.util.List;

/**
 * AST node for Subtypes.
 *
 * Subtypes are Zserio types as well.
 */
public final class Subtype extends DocumentableAstNode implements ZserioType
{
    /**
     * @param location            AST node location.
     * @param pkg                 Package to which belongs the subtype.
     * @param targetTypeReference Type reference to the target type.
     * @param name                Name of the subtype.
     * @param docComments         List of documentation comments belonging to this node.
     */
    public Subtype(AstLocation location, Package pkg, TypeReference targetTypeReference, String name,
            List<DocComment> docComments)
    {
        super(location, docComments);

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
     */
    void resolve()
    {
        if (isResolved)
            return;

        final ZserioType referencedType = typeReference.getType();
        if (referencedType instanceof Subtype)
        {
            baseTypeReference = ((Subtype)referencedType).getBaseTypeReference();
        }
        else if (referencedType instanceof InstantiateType)
        {
            baseTypeReference = ((InstantiateType)referencedType).getTypeReference();
        }
        else
            baseTypeReference = typeReference;

        isResolved = true;
    }

    private final Package pkg;
    private final TypeReference typeReference;
    private final String name;

    private TypeReference baseTypeReference = null;
    private boolean isResolved = false;
}
