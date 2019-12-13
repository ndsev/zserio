package zserio.ast;

import java.util.List;

import zserio.tools.HashUtil;

/**
 * AST node for template argument.
 */
public class TemplateArgument extends AstNodeBase
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

    // TODO[mikir] This should be sorted out because here it shouldn't be solved typeReference equality.
    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof TemplateArgument))
            return false;

        if (this == other)
            return true;

        final TemplateArgument otherArgument = (TemplateArgument)other;

        final String referencedBaseTypeFullName = ZserioTypeUtil.getFullName(referencedBaseType);
        final String otherReferencedBaseTypeFullName =
                ZserioTypeUtil.getFullName(otherArgument.referencedBaseType);

        return referencedBaseTypeFullName.equals(otherReferencedBaseTypeFullName) &&
                typeReference.getTemplateArguments().equals(otherArgument.typeReference.getTemplateArguments());
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, ZserioTypeUtil.getFullName(referencedBaseType));
        for (TemplateArgument templateArgument : typeReference.getTemplateArguments())
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

    /**
     * Resolves template argument.
     */
    void resolve()
    {
        if (isResolved)
            return;

        // We need to "remember" the referenced base type because in case the argument is a template
        // instantiation, the type reference will be resolved further during the template instantiation.
        // TODO[mikir] This is needed only because of getVisibleInstantiateType, should be done somehow better.
        // One possible solution can be to move this logic to InstantiateType. This type should store original
        // referecies.
        referencedBaseType = typeReference.getBaseTypeReference().getType();
        isResolved = true;
    }

    private final TypeReference typeReference;

    private ZserioType referencedBaseType = null;
    private boolean isResolved = false;
}
