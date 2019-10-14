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
     * @param typeReference Reference to the type of the parameter.
     * @param name          Name of the parameter.
     */
    public Parameter(AstLocation location, TypeReference typeReference, String name)
    {
        super(location);

        this.typeReference = typeReference;
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
        typeReference.accept(visitor);
    }

    /**
     * Gets reference to the parameter's type.
     *
     * @return Type reference.
     */
    public TypeReference getTypeReference()
    {
        return typeReference;
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

    /**
     * Instantiate the type parameter.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments Template arguments.
     *
     * @return New type parameter instantiated from this using the given template arguments.
     */
    Parameter instantiate(List<TemplateParameter> templateParameters, List<TypeReference> templateArguments)
    {
        final TypeReference instantiatedTypeReference =
                typeReference.instantiate(templateParameters, templateArguments);

        return new Parameter(getLocation(), instantiatedTypeReference, getName());
    }

    private final TypeReference typeReference;
    private final String name;
}
