package zserio.ast;

import java.util.List;

import zserio.tools.HashUtil;

/**
 * AST node for template argument.
 */
public final class TemplateArgument extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location      AST node location.
     * @param typeReference Referenced type which is a template argument.
     */
    public TemplateArgument(AstLocation location, TypeReference typeReference)
    {
        super(location);

        this.typeReference = typeReference;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitTemplateArgument(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        typeReference.accept(visitor);
    }

    /**
     * Gets reference to type which is a template argument.
     *
     * @return Type reference.
     */
    public TypeReference getTypeReference()
    {
        return typeReference;
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof TemplateArgument))
            return false;

        if (this == other)
            return true;

        final TemplateArgument otherArgument = (TemplateArgument)other;

        final TypeReference baseTypeReference = typeReference.getBaseTypeReference();
        final TypeReference otherBaseTypeReference = otherArgument.typeReference.getBaseTypeReference();

        final String referencedBaseTypeFullName = ZserioTypeUtil.getFullName(baseTypeReference.getType());
        final String otherReferencedBaseTypeFullName =
                ZserioTypeUtil.getFullName(otherBaseTypeReference.getType());

        return referencedBaseTypeFullName.equals(otherReferencedBaseTypeFullName) &&
                baseTypeReference.getTemplateArguments().equals(otherBaseTypeReference.getTemplateArguments());
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        final TypeReference baseTypeReference = typeReference.getBaseTypeReference();
        hash = HashUtil.hash(hash, ZserioTypeUtil.getFullName(baseTypeReference.getType()));
        for (TemplateArgument templateArgument : baseTypeReference.getTemplateArguments())
            hash = HashUtil.hash(hash, templateArgument);

        return hash;
    }

    /**
     * Instantiates the type argument.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments Template arguments.
     *
     * @return New template argument instantiated from this using the given template arguments.
     */
    TemplateArgument instantiate(List<TemplateParameter> templateParameters,
            List<TemplateArgument> templateArguments)
    {
        final TypeReference instantiatedTypeReference = typeReference.instantiate(
                templateParameters, templateArguments);

        return new TemplateArgument(getLocation(), instantiatedTypeReference);
    }

    private final TypeReference typeReference;
}
