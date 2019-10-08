package zserio.ast;

import zserio.ast.TypeReference;
import zserio.ast.ZserioType;

/**
 * AST node for Subtypes.
 *
 * Subtypes are Zserio types as well.
 */
public class Subtype extends DocumentableAstNode implements ZserioType
{
    /**
     * @param location   AST node location.
     * @param pkg        Package to which belongs the subtype.
     * @param targetType Zserio type which belongs to the subtype.
     * @param name       Name of the subtype.
     * @param docComment Documentation comment belonging to this node.
     */
    public Subtype(AstLocation location, Package pkg, ZserioType targetType, String name, DocComment docComment)
    {
        super(location, docComment);

        this.pkg = pkg;
        this.targetType = targetType;
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

        targetType.accept(visitor);
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
     * Gets the target type.
     *
     * @return Type referenced by this subtype.
     */
    // TODO[Mi-L@]: Rename to getReferencedType
    public ZserioType getTargetType()
    {
        return targetType;
    }

    /**
     * Gets reference to the base type.
     *
     * @return Reference to base type of the target type.
     */
    public ZserioType getBaseTypeReference()
    {
        return baseTypeReference;
    }

    /**
     * Resolves the subtype to a defined type called at the end of linking phase.
     *
     * @return Resolved reference to base type of this subtype.
     */
    ZserioType resolve()
    {
        if (resolvingState == ResolvingState.RESOLVED)
            return baseTypeReference;

        // detect cycles in subtype definitions
        if (resolvingState == ResolvingState.RESOLVING)
            throw new ParserException(this, "Cyclic dependency detected in subtype '" +
                    getName() + "' definition!");

        resolvingState = ResolvingState.RESOLVING;

        // base type can be only type reference or a defined type.
        if (targetType instanceof TypeReference)
        {
            final TypeReference targetTypeReference = (TypeReference)targetType;
            targetTypeReference.resolve(); // make sure the type reference is resolved

            final ZserioType referencedTargetType = targetTypeReference.getReferencedType();
            if (referencedTargetType instanceof Subtype)
                baseTypeReference = ((Subtype)referencedTargetType).resolve();
            else
                baseTypeReference = targetTypeReference;
        }
        else // built-in type
        {
            baseTypeReference = targetType;
        }

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
    private final ZserioType targetType;
    private final String name;

    private ResolvingState resolvingState = ResolvingState.UNRESOLVED;
    private ZserioType baseTypeReference = null;
}
