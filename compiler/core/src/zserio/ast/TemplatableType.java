package zserio.ast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
     * @param pkg Package to which belongs the compound type.
     * @param name Name of the compound type.
     * @param templateParameters List of template parameters.
     * @param typeParameters List of parameters for the compound type.
     * @param fields List of all fields of the compound type.
     * @param functions List of all functions of the compound type.
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
        return Collections.unmodifiableList(new ArrayList<ZserioTemplatableType>(instantiationsMap.values()));
    }

    @Override
    public ZserioTemplatableType getTemplate()
    {
        return template;
    }

    @Override
    public Iterable<TypeReference> getInstantiationReferenceStack()
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
    InstantiationResult instantiate(ArrayDeque<TypeReference> instantiationReferenceStack)
    {
        try
        {
            final TypeReference instantiationReference = instantiationReferenceStack.peek();
            final List<TypeReference> templateArguments = instantiationReference.getTemplateArguments();
            if (templateParameters.size() != templateArguments.size())
            {
                throw new ParserException(instantiationReference,
                        "Wrong number of template arguments for template '" + getName() + "'! Expecting " +
                        templateParameters.size() + ", got " + templateArguments.size() + "!");
            }

            final List<TypeReference> resolvedTemplateArguments = resolveTemplateArguments(templateArguments);
            final List<TemplateArgument> wrappedTemplateArguments =
                    wrapTemplateArguments(resolvedTemplateArguments);

            TemplatableType instantiation = instantiationsMap.get(wrappedTemplateArguments);
            boolean isNewInstance = false;
            if (instantiation == null)
            {
                final String name = getInstantiationNameImpl(wrappedTemplateArguments);
                checkInstantiationName(wrappedTemplateArguments, name, instantiationReference.getLocation());
                instantiation = instantiateImpl(name, resolvedTemplateArguments);
                instantiation.instantiationReferenceStack = instantiationReferenceStack.clone();
                instantiation.template = this;
                instantiationsMap.put(wrappedTemplateArguments, instantiation);
                instantiationsNamesMap.put(name, instantiation);
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
     * Returns instantiation name for the actual template parameters.
     *
     * @param templateArguments Actual template parameters.
     *
     * @return Instantiation name.
     */
    String getInstantiationName(List<TypeReference> templateArguments)
    {
        return getInstantiationNameImpl(wrapTemplateArguments(templateArguments));
    }

    /**
     * Concrete implementation of template instantiation.
     *
     * @param templateArguments Actual template parameters.
     */
    abstract TemplatableType instantiateImpl(String name, List<TypeReference> templateArguments);

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

    private String getInstantiationNameImpl(List<TemplateArgument> wrappedTemplateArguments)
    {
        final StringBuilder nameBuilder = new StringBuilder(getName());

        for (TemplateArgument templateArgument : wrappedTemplateArguments)
        {
            nameBuilder.append(NAME_SEPARATOR);
            nameBuilder.append(templateArgument.toString());
        }

        return nameBuilder.toString();
    }

    private void checkInstantiationName(List<TemplateArgument> templateArguments, String name,
            AstLocation instantiationLocation)
    {
        final TemplatableType prevInstantiation = instantiationsNamesMap.get(name);
        if (prevInstantiation != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(getLocation(),
                    "Instantiation name '" + name + "' already exits!");

            final Iterator<TypeReference> descendingIterator =
                    prevInstantiation.instantiationReferenceStack.descendingIterator();
            while (descendingIterator.hasNext())
            {
                final TypeReference instantiationReference = descendingIterator.next();
                stackedException.pushMessage(instantiationReference.getLocation(),
                        descendingIterator.hasNext()
                                ? "    Required in instantiation of '" +
                                        instantiationReference.getReferencedTypeName() + "' from here"
                                : "    First instantiated here");
            }
            throw stackedException;
        }
    }

    private List<TypeReference> resolveTemplateArguments(List<TypeReference> templateArguments)
    {
        final List<TypeReference> resolvedTemplateArguments = new ArrayList<TypeReference>();
        for (TypeReference templateArgument : templateArguments)
        {
            TypeReference resolvedTemplateArgument = templateArgument;
            final ZserioType referencedTemplateArgument = templateArgument.getType();
            if (referencedTemplateArgument instanceof Subtype)
                resolvedTemplateArgument = ((Subtype)referencedTemplateArgument).getBaseTypeReference();
            resolvedTemplateArguments.add(resolvedTemplateArgument);
        }

        return resolvedTemplateArguments;
    }

    private List<TemplateArgument> wrapTemplateArguments(List<TypeReference> templateArguments)
    {
        final List<TemplateArgument> wrappedTemplateArguments = new ArrayList<TemplateArgument>();
        for (TypeReference templateArgument : templateArguments)
            wrappedTemplateArguments.add(new TemplateArgument(templateArgument));

        return wrappedTemplateArguments;
    }

    private static class TemplateArgument
    {
        public TemplateArgument(TypeReference templateArgument)
        {
            packageName = templateArgument.getReferencedPackageName();
            typeName = templateArgument.getReferencedTypeName();
            for (TypeReference argument: templateArgument.getTemplateArguments())
                templateArguments.add(new TemplateArgument(argument));

            // TODO[Mi-L@]: Get rid of getPackage() on ZserioType interface!
            //              ZserioType can have only reference to the package name.
            if (templateArgument.getType() instanceof BuiltInType)
                resolvedPackageName = PackageName.EMPTY;
            else
                resolvedPackageName = templateArgument.getType().getPackage().getPackageName();
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof TemplateArgument))
                return false;

            if (this == other)
                return true;

            final TemplateArgument otherArgument = (TemplateArgument)other;
            return resolvedPackageName.equals(otherArgument.resolvedPackageName) &&
                    typeName.equals(otherArgument.typeName) &&
                    templateArguments.equals(otherArgument.templateArguments);
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, resolvedPackageName);
            hash = HashUtil.hash(hash, typeName);
            hash = HashUtil.hash(hash, templateArguments);
            return hash;
        }

        @Override
        public String toString()
        {
            final StringJoinUtil.Joiner joiner = new StringJoinUtil.Joiner(NAME_SEPARATOR);
            joiner.append(packageName.toString(NAME_SEPARATOR));
            joiner.append(typeName);
            for (TemplateArgument templateArgument : templateArguments)
                joiner.append(templateArgument.toString());

            return joiner.toString();
        }

        private final PackageName resolvedPackageName;
        private final PackageName packageName;
        private final String typeName;
        private final List<TemplateArgument> templateArguments = new ArrayList<TemplateArgument>();
    }

    private final List<TemplateParameter> templateParameters;
    private final Map<List<TemplateArgument>, TemplatableType> instantiationsMap =
            new HashMap<List<TemplateArgument>, TemplatableType>();
    private final Map<String, TemplatableType> instantiationsNamesMap =
            new HashMap<String, TemplatableType>();
    private ArrayDeque<TypeReference> instantiationReferenceStack = null;
    private TemplatableType template = null;

    private static final String NAME_SEPARATOR = "_";
}
