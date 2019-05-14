package zserio.ast;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;

/**
 * AST node for Subtypes.
 *
 * Subtypes are Zserio types as well.
 */
public class Subtype extends AstNodeWithDoc implements ZserioType
{
    /**
     * @param token      ANTLR4 token to localize AST node in the sources.
     * @param pkg        Package to which belongs the subtype.
     * @param targetType Zserio type which belongs to the subtype.
     * @param name       Name of the subtype.
     * @param docComment Documentation comment belonging to this node.
     */
    public Subtype(Token token, Package pkg, ZserioType targetType, String name, DocComment docComment)
    {
        super(token, docComment);

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
        targetType.accept(visitor);

        super.visitChildren(visitor);
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
    public ZserioType getTargetType()
    {
        return targetType;
    }

    /**
     * Gets target base type.
     *
     * @return Resolved base type of the target type.
     */
    public ZserioType getTargetBaseType()
    {
        return targetBaseType;
    }

    /**
     * Resolves the subtype to a defined type called at the end of linking phase.
     *
     * @return Resolved base type of this subtype.
     */
    ZserioType resolve()
    {
        if (resolvingState == ResolvingState.RESOLVED)
            return targetBaseType;

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
                targetBaseType = ((Subtype)referencedTargetType).resolve();
            else
                targetBaseType = referencedTargetType;
        }
        else // built-in type
        {
            targetBaseType = targetType;
        }

        resolvingState = ResolvingState.RESOLVED;

        return targetBaseType;
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
    private ZserioType targetBaseType = null;
}
