package zserio.ast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zserio.tools.HashUtil;
import zserio.tools.StringJoinUtil;

/**
 * AST abstract node for all templatable types.
 */
abstract class TemplatableType extends DocumentableAstNode implements ZserioTemplatableType
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param templateParameters List of template parameters.
     * @param docComment Documentation comment belonging to this node.
     */
    public TemplatableType(AstLocation location, List<TemplateParameter> templateParameters,
            DocComment docComment)
    {
        super(location, docComment);

        this.templateParameters = templateParameters;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        for (TemplateParameter templateParameter : templateParameters)
            templateParameter.accept(visitor);
    }

    @Override
    public List<TemplateParameter> getTemplateParameters()
    {
        return templateParameters;
    }

    @Override
    public List<ZserioTemplatableType> getInstantiations()
    {
        return Collections.unmodifiableList(new ArrayList<ZserioTemplatableType>(instantiationMap.values()));
    }

    @Override
    public ZserioTemplatableType getTemplate()
    {
        return template;
    }

    @Override
    public ArrayDeque<TypeReference> getInstantiationReferenceStack()
    {
        return instantiationReferenceStack.clone();
    }

    /**
     * Instantiates the referenced template using the given instantiation reference.
     *
     * @param instantiationReference Instantiation reference.
     *
     * @return Instantiation result.
     */
    InstantiationResult instantiate(ArrayDeque<TypeReference> instantiationReferenceStack,
            Package instantiationPackage, String instantiationName)
    {
        try
        {
            final TypeReference instantiationReference = instantiationReferenceStack.peek();
            final List<TemplateArgument> templateArguments = instantiationReference.getTemplateArguments();
            if (templateParameters.size() != templateArguments.size())
            {
                throw new ParserException(instantiationReference,
                        "Wrong number of template arguments for template '" + getName() + "'! Expecting " +
                        templateParameters.size() + ", got " + templateArguments.size() + "!");
            }

            final InstantiationMapKey key = new InstantiationMapKey(
                    instantiationPackage.getPackageName(), templateArguments);

            TemplatableType instantiation = instantiationMap.get(key);
            boolean isNewInstance = false;
            if (instantiation == null)
            {
                final String name = instantiationName != null ? instantiationName :
                        generateInstantiationName(templateArguments);
                instantiation = instantiateImpl(name, templateArguments, instantiationPackage);
                instantiation.instantiationReferenceStack = instantiationReferenceStack.clone();
                instantiation.template = this;
                instantiationPackage.addTemplateInstantiation(name, instantiation);
                instantiationMap.put(key, instantiation);
                isNewInstance = true;
            }

            return new InstantiationResult(instantiation, isNewInstance);
        }
        catch (ParserException e)
        {
            throw new InstantiationException(e,  instantiationReferenceStack);
        }
    }

    /**
     * Concrete implementation of template instantiation.
     *
     * @param name                 Name to use as instantiation name.
     * @param templateArguments    Actual template parameters.
     * @parma instantiationPackage Package where to instantiate the template.
     */
    abstract TemplatableType instantiateImpl(String name, List<TemplateArgument> templateArguments,
            Package instantiationPackage);

    /**
     * Definition of result returned from instantiate() method.
     */
    static final class InstantiationResult
    {
        /**
         * Constructor.
         *
         * @param instantiation Instantiation type to create from.
         * @param isNewInstance Flag if this instantiation is newly created.
         */
        public InstantiationResult(ZserioTemplatableType instantiation, boolean isNewInstance)
        {
            this.instantiation = instantiation;
            this.isNewInstance = isNewInstance;
        }

        /**
         * Gets instantiation type.
         *
         * @return Instantiation type.
         */
        public ZserioTemplatableType getInstantiation()
        {
            return instantiation;
        }

        /**
         * Gets new instance flag.
         *
         * @return True if the instantiation is newly created.
         */
        public boolean isNewInstance()
        {
            return isNewInstance;
        }

        private final ZserioTemplatableType instantiation;
        private final boolean isNewInstance;
    }

    private static class InstantiationMapKey
    {
        public InstantiationMapKey(PackageName instantiationPackageName,
                List<TemplateArgument> templateArguments)
        {
            this.instantiationPackageName = instantiationPackageName;
            this.templateArguments = templateArguments;
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof InstantiationMapKey))
                return false;

            if (this == other)
                return true;

            final InstantiationMapKey otherKey = (InstantiationMapKey)other;
            return instantiationPackageName.equals(otherKey.instantiationPackageName) &&
                    templateArguments.equals(otherKey.templateArguments);
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, instantiationPackageName);
            hash = HashUtil.hash(hash, templateArguments);
            return hash;
        }

        private final PackageName instantiationPackageName;
        private final List<TemplateArgument> templateArguments;
    }

    private String generateInstantiationName(List<TemplateArgument> templateArguments)
    {
        final StringBuilder nameBuilder = new StringBuilder(getName());

        appendTemplateArgumentsToName(nameBuilder, templateArguments);

        return nameBuilder.toString();
    }

    private void appendTemplateArgumentsToName(StringBuilder nameBuilder,
            List<TemplateArgument> templateArguments)
    {
        for (TemplateArgument templateArgument : templateArguments)
        {
            nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
            appendTemplateArgumentToName(nameBuilder, templateArgument);
        }
    }

    private void appendTemplateArgumentToName(StringBuilder nameBuilder, TemplateArgument templateArgument)
    {
        TypeReference typeReference = templateArgument.getTypeReference();
        final ZserioType type = typeReference.getType();
        if (type instanceof Subtype)
            typeReference = ((Subtype)type).getBaseTypeReference();

        final StringJoinUtil.Joiner joiner = new StringJoinUtil.Joiner(TEMPLATE_NAME_SEPARATOR);
        joiner.append(typeReference.getReferencedPackageName().toString(TEMPLATE_NAME_SEPARATOR));
        joiner.append(typeReference.getReferencedTypeName());
        nameBuilder.append(joiner.toString());

        appendTemplateArgumentsToName(nameBuilder, templateArgument.getTypeReference().getTemplateArguments());
    }

    private final List<TemplateParameter> templateParameters;
    private final Map<InstantiationMapKey, TemplatableType> instantiationMap =
            new HashMap<InstantiationMapKey, TemplatableType>();
    private ArrayDeque<TypeReference> instantiationReferenceStack = null;
    private TemplatableType template = null;

    private static final String TEMPLATE_NAME_SEPARATOR = "_";
}
