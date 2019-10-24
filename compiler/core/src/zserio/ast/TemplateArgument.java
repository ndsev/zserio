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
     * @param location     AST node location.
     * @param typeRefrence Referenced type which is a template argument.
     */
    public TemplateArgument(AstLocation location, TypeReference typeRefrence)
    {
        super(location);

        this.typeReference = typeRefrence;
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

        final ZserioType baseType = typeReference.getBaseType();
        final PackageName packageName = baseType instanceof BuiltInType ?
                PackageName.EMPTY : baseType.getPackage().getPackageName();

        final ZserioType otherBaseType = otherArgument.typeReference.getBaseType();
        final PackageName otherPackageName = otherBaseType instanceof BuiltInType ?
                PackageName.EMPTY : otherBaseType.getPackage().getPackageName();

        return packageName.equals(otherPackageName) &&
                baseType.getName().equals(otherBaseType.getName()) &&
                typeReference.getTemplateArguments().equals(otherArgument.typeReference.getTemplateArguments());
    }

    @Override
    public int hashCode()
    {
        final ZserioType baseType = typeReference.getBaseType();
        final PackageName packageName = baseType instanceof BuiltInType ?
                PackageName.EMPTY : baseType.getPackage().getPackageName();

        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, packageName);
        hash = HashUtil.hash(hash, baseType.getName());
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

    private final TypeReference typeReference;
}
