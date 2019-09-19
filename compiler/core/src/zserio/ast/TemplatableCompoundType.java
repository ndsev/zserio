package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zserio.antlr.util.ParserException;
import zserio.tools.HashUtil;
import zserio.tools.StringJoinUtil;

abstract class TemplatableCompoundType extends CompoundType implements ZserioTemplatableType
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param pkg Package to which belongs the compound type.
     * @param name Name of the compound type.
     * @param templateParameters List of template parameteres.
     * @param typeParameters List of parameters for the compound type.
     * @param fields List of all fields of the compound type.
     * @param functions List of all functions of the compound type.
     * @param docComment Documentation comment belonging to this node.
     */
    TemplatableCompoundType(AstLocation location, Package pkg, String name, List<String> templateParameters,
            List<Parameter> typeParameters, List<Field> fields, List<FunctionType> functions,
            DocComment docComment)
    {
        super(location, pkg, name, typeParameters, fields, functions, docComment);

        this.templateParameters = templateParameters;
    }

    @Override
    public List<String> getTemplateParameters()
    {
        return templateParameters;
    }

    @Override
    public ZserioTemplatableType instantiate(List<ZserioType> templateArguments) // TODO[Mi-L@]: Why public?
    {
        if (getTemplateParameters().size() != templateArguments.size()) // TODO[Mi-L@]: Improve message!
            throw new ParserException(this, "Wrong number of template arguments!");

        final List<TemplateArgument> wrappedTemplateArguments = wrapTemplateArguments(templateArguments);

        // TODO[Mi-L@]: Currently we doesn't resolve subtypes. But it might be possible to do so.
        //              So currently template instantiation for a subtype argument will be different
        //              than instantiation for the base type.
        TemplatableCompoundType instantiation = instantiationsMap.get(wrappedTemplateArguments);
        if (instantiation == null)
        {
            final String name = getInstantiationNameImpl(wrappedTemplateArguments);
            checkInstantiationName(wrappedTemplateArguments, name);
            instantiation = instantiateImpl(name, templateArguments);
            // TODO[Mi-L@]: We should probably remember also the instantiation reference (for error reporting)?
            instantiation.template = this;
            instantiationsMap.put(wrappedTemplateArguments, instantiation);
        }

        return instantiation;
    }

    @Override
    public List<ZserioTemplatableType> getInstantiations()
    {
        // TODO[Mi-L@]: Do we really need List? Isn't Collection enough? Do we need it public?
        return Collections.unmodifiableList(new ArrayList<ZserioTemplatableType>(instantiationsMap.values()));
    }

    @Override
    public ZserioTemplatableType getInstantiation(List<ZserioType> templateArguments)
    {
        final ZserioTemplatableType instantiation =
                instantiationsMap.get(wrapTemplateArguments(templateArguments));
        if (instantiation == null)
            throw new ParserException(this, "No instantiation found!"); // TODO[Mi-L@]: Improve message!

        return instantiation;
    }

    @Override
    public ZserioTemplatableType getTemplate()
    {
        return template;
    }

    String getInstantiationName(List<ZserioType> templateArguments)
    {
        return getInstantiationNameImpl(wrapTemplateArguments(templateArguments));
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

    private void checkInstantiationName(List<TemplateArgument> templateArguments, String name)
    {
        // TODO[Mi-L@]: Improve message!
        if (!instantiationsNamesSet.add(name))
            throw new ParserException(this, "Clash in instantiations name found: '" + name + "'!");
    }

    private List<TemplateArgument> wrapTemplateArguments(List<ZserioType> templateArguments)
    {
        List<TemplateArgument> wrappedTemplateArguments = new ArrayList<TemplateArgument>();
        for (ZserioType templateArgument : templateArguments)
            wrappedTemplateArguments.add(new TemplateArgument(getPackage(), templateArgument));

        return wrappedTemplateArguments;
    }

    private static class TemplateArgument
    {
        public TemplateArgument(Package ownerPackage, ZserioType templateArgument)
        {
            this.ownerPackage = ownerPackage;

            if (templateArgument instanceof TypeReference)
            {
                final TypeReference referencedArgument = (TypeReference)templateArgument;
                final ZserioType type = ownerPackage.getVisibleType(
                        referencedArgument.getReferencedPackageName(),
                        referencedArgument.getReferencedTypeName());
                if (type == null) // TODO[Mi-L@]: Improve message, add test.
                    throw new ParserException(templateArgument, "Cannot find template argument type!");

                packageName = type.getPackage().getPackageName();
                typeName = type.getName();
                for (ZserioType argument: referencedArgument.getTemplateArguments())
                    templateArguments.add(new TemplateArgument(ownerPackage, argument));
            }
            else
            {
                // built-in type
                packageName = PackageName.EMPTY;
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
            return packageName.equals(otherArgument.packageName) && typeName.equals(otherArgument.typeName) &&
                    templateArguments.equals(otherArgument.templateArguments);
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, packageName);
            hash = HashUtil.hash(hash, typeName);
            hash = HashUtil.hash(hash, templateArguments);
            return hash;
        }

        @Override
        public String toString()
        {
            StringJoinUtil.Joiner joiner = new StringJoinUtil.Joiner(NAME_SEPARATOR);
            // TODO[Mi-L@]: Check the logic of omitting the package name!
            if (ownerPackage.getPackageName() != packageName)
                joiner.append(packageName.toString(NAME_SEPARATOR));
            joiner.append(typeName);
            for (TemplateArgument templateArgument : templateArguments)
                joiner.append(templateArgument.toString());

            return joiner.toString();
        }

        private final Package ownerPackage;

        private final PackageName packageName;
        private final String typeName;
        private final List<TemplateArgument> templateArguments = new ArrayList<TemplateArgument>();
    }

    /**
     * Concrete implementation of template instantiation.
     *
     * @param templateArguemnts Actual template parameters.
     */
    abstract TemplatableCompoundType instantiateImpl(String name, List<ZserioType> templateArguemnts);

    private final List<String> templateParameters;
    private final Map<List<TemplateArgument>, TemplatableCompoundType> instantiationsMap =
            new HashMap<List<TemplateArgument>, TemplatableCompoundType>();
    private final Set<String> instantiationsNamesSet = new HashSet<String>();
    private ZserioTemplatableType template = null;

    private static final String NAME_SEPARATOR = "_";
}
