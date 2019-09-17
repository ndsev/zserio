package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zserio.antlr.util.ParserException;

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
    public void instantiate(List<ZserioType> templateArguments)
    {
        if (getTemplateParameters().size() != templateArguments.size())
            throw new ParserException(this, "Wrong number of template arguments!"); // TODO:

        final String name = getInstantiationName(templateArguments);

        if (!instantiationsMap.containsKey(name))
        {
            final TemplatableCompoundType instantiation = instantiateImpl(name, templateArguments);
            instantiation.template = this;
            instantiationsMap.put(name, instantiation);
        }
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
                instantiationsMap.get(getInstantiationName(templateArguments));
        if (instantiation == null)
            throw new ParserException(this, "No instantiation found!"); // TODO[Mi-L@]

        return instantiation;
    }

    @Override
    public ZserioTemplatableType getTemplate()
    {
        return template;
    }

    String getInstantiationName(List<ZserioType> templateArguments)
    {
        // TODO[Mi-L@]: Generate unbreakable name.
        final StringBuilder name = new StringBuilder(getName());
        for (ZserioType argument : templateArguments)
        {
            name.append("_");
            name.append(ZserioTypeUtil.getFullName(argument).replaceAll("\\.", "_"));
        }
        return name.toString();
    }

    /**
     * Concrete implementation of template instantiation.
     *
     * @param templateArguemnts Actual template parameters.
     */
    abstract TemplatableCompoundType instantiateImpl(String name, List<ZserioType> templateArguemnts);

    private final List<String> templateParameters;
    private final Map<String, ZserioTemplatableType> instantiationsMap =
            new HashMap<String, ZserioTemplatableType>();
    private ZserioTemplatableType template = null;
}
