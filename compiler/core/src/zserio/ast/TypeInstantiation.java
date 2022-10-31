package zserio.ast;

import java.util.List;

import zserio.tools.WarningsConfig;

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
            final ParserStackedException exception = new ParserStackedException(
                    typeReference.getLocation(), "Referenced type '" +
                    ZserioTypeUtil.getReferencedFullName(typeReference) +
                    "' is defined as parameterized type!");
            fillInstantiationStack(exception);
            throw exception;
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
    void check(WarningsConfig config, ZserioTemplatableType currentTemplateInstantiation)
    {
        // overridden by particular instantiations
    }

    protected void fillInstantiationStack(ParserStackedException exception)
    {
        TypeReference resolvingTypeReference = typeReference;
        ZserioType resolvingType = resolvingTypeReference.getType();
        while (resolvingType instanceof Subtype || resolvingType instanceof InstantiateType)
        {
            if (resolvingType instanceof Subtype)
            {
                exception.pushMessage(resolvingType.getLocation(),
                        "    See subtype '" +
                        ZserioTypeUtil.getReferencedFullName(resolvingTypeReference) + "' definition here:");
                resolvingTypeReference = ((Subtype)resolvingType).getTypeReference();
            }
            else if (resolvingType instanceof InstantiateType)
            {
                exception.pushMessage(resolvingType.getLocation(),
                        "    See template instantiation '" +
                        ZserioTypeUtil.getReferencedFullName(resolvingTypeReference) + "' definition here:");
                resolvingTypeReference = ((InstantiateType)resolvingType).getTypeReference();
            }
            resolvingType = resolvingTypeReference.getType();
        }

        exception.pushMessage(resolvingType.getLocation(),
                    "    See '" + resolvingType.getName() + "' definition here:");
    }

    private final TypeReference typeReference;
}
