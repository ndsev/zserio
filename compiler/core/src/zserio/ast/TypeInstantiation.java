package zserio.ast;

import java.util.List;

/**
 * AST node for type instantiation.
 */
public class TypeInstantiation extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location      AST node location.
     * @param typeReference Reference to the instantiated type definition.
     */
    public TypeInstantiation(AstLocation location, TypeReference typeReference)
    {
        super(location);

        this.typeReference = typeReference;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitTypeInstantiation(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        typeReference.accept(visitor);
    }

    /**
     * Gets the instantiated type.
     *
     * @return Zserio type which is instantiated by this type instantiation.
     */
    public ZserioType getType()
    {
        return typeReference.getType();
    }

    /**
     * Gets base type - i.e. the type got by resolving subtypes.
     *
     * @return Zserio base type.
     */
    public ZserioType getBaseType()
    {
        return typeReference.getBaseTypeReference().getType();
    }

    /**
     * Gets reference to the instantiated type.
     *
     * @return Type reference.
     */
    public TypeReference getTypeReference()
    {
        return typeReference;
    }

    /**
     * Instantiate the type instantiation.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments  Template arguments.
     *
     * @return New type instantiation instantiated from this using the given template arguments.
     */
    TypeInstantiation instantiate(List<TemplateParameter> templateParameters,
            List<TemplateArgument> templateArguments)
    {
        final TypeReference instantiatedTypeReference =
                typeReference.instantiate(templateParameters, templateArguments);

        return instantiateImpl(templateParameters, templateArguments, instantiatedTypeReference);
    }

    /**
     * Instantiate the particular type instantiation.
     *
     * @param templateParameters         Template parameters.
     * @param templateArguments          Template arguments.
     * @param instantiatedTypeReference  Instantiated type reference.
     *
     * @return New type instantiation instantiated from this using the given template arguments.
     */
    TypeInstantiation instantiateImpl(List<TemplateParameter> templateParameters,
            List<TemplateArgument> templateArguments, TypeReference instantiatedTypeReference)
    {
        return new TypeInstantiation(getLocation(), instantiatedTypeReference);
    }

    /**
     * Resolves the type instantiation.
     */
    void resolve()
    {
        // overridden by particular instantiations
        final ZserioType baseType = this.getBaseType();

        final boolean isParameterized =
                baseType instanceof CompoundType && !((CompoundType)baseType).getTypeParameters().isEmpty();
        if (isParameterized)
        {
            throw new ParserException(typeReference, "Referenced type '" +
                    ZserioTypeUtil.getReferencedFullName(typeReference) +
                    "' is defined as parameterized type!");
        }

        if (baseType instanceof DynamicBitFieldType)
            throw new ParserException(typeReference, "Missing length for the dynamic bitfield type!");
    }

    /**
     * Evaluates the type instantiation.
     */
    void evaluate()
    {
        // overridden by particular instantiations
    }

    /**
     * Checks the type instantiation.
     */
    void check()
    {
        // overridden by particular instantiations
    }

    private final TypeReference typeReference;
}
