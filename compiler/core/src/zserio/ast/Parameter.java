package zserio.ast;

import java.util.List;

/**
 * AST node for a parameter defined in the parameterized compound types.
 */
public class Parameter extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location      AST node location.
     * @param parameterType Zserio type of the parameter.
     * @param name          Name of the parameter.
     */
    public Parameter(AstLocation location, ZserioType parameterType, String name)
    {
        super(location);

        this.parameterType = parameterType;
        this.name = name;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitParameter(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        parameterType.accept(visitor);
    }

    /**
     * Gets parameter Zserio type.
     *
     * @return Parameter type.
     */
    public ZserioType getParameterType()
    {
        return parameterType;
    }

    /**
     * Gets parameter name.
     *
     * @return Parameter name.
     */
    public String getName()
    {
        return name;
    }

    Parameter instantiate(List<String> templateParameters, List<ZserioType> templateArguments)
    {
        int index = templateParameters.indexOf(parameterType.getName());
        final ZserioType instantiatedParameterType = (index != -1) ? templateArguments.get(index) :
                parameterType;

        // TODO[Mi-L@]: What if parameter is a tempalte? How it works?

        return new Parameter(getLocation(), instantiatedParameterType, getName());
    }

    private final ZserioType parameterType;
    private final String name;
}
