package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zserio.antlr.util.ParserException;
import zserio.tools.HashUtil;
import zserio.tools.StringJoinUtil;
import zserio.tools.ZserioToolPrinter;

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
    public AstLocation getInstantiationLocation()
    {
        return instantiationReference == null ? null : instantiationReference.getLocation();
    }

    /**
     * Instantiates the referenced template using the given instantiation reference.
     *
     * @param instantiationReference Instantiation reference.
     *
     * @return Instantiated template.
     */
    ZserioTemplatableType instantiate(TypeReference instantiationReference)
    {
        final List<ZserioType> templateArguments = instantiationReference.getTemplateArguments();
        if (templateParameters.size() != templateArguments.size())
        {
            throw new ParserException(instantiationReference,
                    "Wrong number of template arguments for template '" + getName() + "'! Expecting " +
                    templateParameters.size() + ", got " + templateArguments.size() + "!");
        }

        final List<TemplateArgument> wrappedTemplateArguments = wrapTemplateArguments(templateArguments);

        // TODO[Mi-L@]: Currently we doesn't resolve subtypes. But it might be possible to do so.
        //              So currently template instantiation for a subtype argument will be different
        //              than instantiation for the base type.
        TemplatableType instantiation = instantiationsMap.get(wrappedTemplateArguments);
        if (instantiation == null)
        {
            final String name = getInstantiationNameImpl(wrappedTemplateArguments);
            checkInstantiationName(wrappedTemplateArguments, name, instantiationReference.getLocation());
            instantiation = instantiateImpl(name, templateArguments);
            instantiation.instantiationReference = instantiationReference;
            instantiation.template = this;
            instantiationsMap.put(wrappedTemplateArguments, instantiation);
            instantiationsNamesMap.put(name, instantiation);
        }

        return instantiation;
    }

    /**
     * Returns instantiation name for the actual template parameters.
     *
     * @param templateArguments Actual template parameters.
     *
     * @return Instantiation name.
     */
    String getInstantiationName(List<ZserioType> templateArguments)
    {
        return getInstantiationNameImpl(wrapTemplateArguments(templateArguments));
    }

    /**
     * Concrete implementation of template instantiation.
     *
     * @param templateArguemnts Actual template parameters.
     */
    abstract TemplatableType instantiateImpl(String name, List<ZserioType> templateArguemnts);

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
        final ZserioTemplatableType prevInstantiation = instantiationsNamesMap.get(name);
        if (prevInstantiation != null)
        {
            ZserioToolPrinter.printError(instantiationLocation,
                    "In instantiation of '" + getName() + "' required from here");
            ZserioToolPrinter.printError(prevInstantiation.getInstantiationLocation(),
                    "First instantiated from here");
            throw new ParserException(this, "Instantiation name '" + name + "' already exits!");
        }
    }

    private List<TemplateArgument> wrapTemplateArguments(List<ZserioType> templateArguments)
    {
        final List<TemplateArgument> wrappedTemplateArguments = new ArrayList<TemplateArgument>();
        for (ZserioType templateArgument : templateArguments)
            wrappedTemplateArguments.add(new TemplateArgument(templateArgument));

        return wrappedTemplateArguments;
    }

    private static class TemplateArgument
    {
        public TemplateArgument(ZserioType templateArgument)
        {
            if (templateArgument instanceof TypeReference)
            {
                final TypeReference referencedArgument = (TypeReference)templateArgument;

                packageName = referencedArgument.getReferencedPackageName();
                typeName = referencedArgument.getReferencedTypeName();
                for (ZserioType argument: referencedArgument.getTemplateArguments())
                    templateArguments.add(new TemplateArgument(argument));

                // TODO[Mi-L@]: Use getPackage().getPackageName() when resolving is refactored.
                Package ownerPackage = referencedArgument.getOwnerPackage();
                final ZserioType resolvedArgument = ownerPackage.getVisibleType(packageName, typeName);
                if (resolvedArgument == null)
                {
                    throw new ParserException(referencedArgument, "Unresolved referenced type '" +
                            ZserioTypeUtil.getReferencedFullName(referencedArgument) + "'!");
                }
                resolvedPackageName = resolvedArgument.getPackage().getPackageName();
            }
            else
            {
                // built-in type
                packageName = PackageName.EMPTY;
                resolvedPackageName = packageName;
                typeName = templateArgument.getName();
            }
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
    private TypeReference instantiationReference = null;
    private TemplatableType template = null;

    private static final String NAME_SEPARATOR = "_";
}
