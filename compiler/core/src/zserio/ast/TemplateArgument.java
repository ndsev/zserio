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

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof TemplateArgument))
            return false;

        if (this == other)
            return true;

        final TemplateArgument otherArgument = (TemplateArgument)other;

        final PackageName packageName = referencedBaseType instanceof BuiltInType ?
                PackageName.EMPTY : referencedBaseType.getPackage().getPackageName();

        final ZserioType otherBaseType = otherArgument.referencedBaseType;
        final PackageName otherPackageName = otherBaseType instanceof BuiltInType ?
                PackageName.EMPTY : otherBaseType.getPackage().getPackageName();

        return packageName.equals(otherPackageName) &&
                referencedBaseType.getName().equals(otherBaseType.getName()) &&
                typeReference.getTemplateArguments().equals(otherArgument.typeReference.getTemplateArguments());
    }

    @Override
    public int hashCode()
    {
        final PackageName packageName = referencedBaseType instanceof BuiltInType ?
                PackageName.EMPTY : referencedBaseType.getPackage().getPackageName();

        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, packageName);
        hash = HashUtil.hash(hash, referencedBaseType.getName());
        hash = HashUtil.hash(hash, typeReference.getTemplateArguments());
        return hash;
    }

    /**
     * Instantiate the type reference.
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
        // we need to "remember" the referenced base type because in case the argument is a template
        // instantiation, the type reference will be resolved further during the template instantiation
        referencedBaseType = typeReference.getBaseType();
    }

    private final TypeReference typeReference;

    private ZserioType referencedBaseType = null;
}
