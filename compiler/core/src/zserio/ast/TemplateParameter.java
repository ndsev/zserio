package zserio.ast;

import java.util.List;

/**
 * AST node for template parameters.
 *
 * Template parameters are just wrapped strings.
 */
public final class TemplateParameter extends AstNodeBase implements ZserioType
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param name     Name of the template parameter.
     */
    public TemplateParameter(AstLocation location, String name)
    {
        super(location);

        this.name = name;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitTemplateParameter(this);
    }

    @Override
    public Package getPackage()
    {
        // TODO: template parameter types do not have any package,
        // because it's not a package symbol, it's a scope symbol
        throw new InternalError("TemplateParameter.getPackage() is not implemented!");
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Get index of the given template parameter name within the given template parameters list.
     *
     * @param templateParameters List of template parameters.
     * @param name               Template parameter name to search for.
     *
     * @return Index of the given template parameter name if it is present in the list, -1 otherwise.
     */
    static int indexOf(List<TemplateParameter> templateParameters, String name)
    {
        for (int i = 0; i < templateParameters.size(); ++i)
        {
            if (templateParameters.get(i).getName().equals(name))
                return i;
        }

        return -1;
    }

    private final String name;
}