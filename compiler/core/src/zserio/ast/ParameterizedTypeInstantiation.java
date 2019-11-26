package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AST node for parameterized type instantiation.
 */
public class ParameterizedTypeInstantiation extends TypeInstantiation
{
    /**
     * Constructor.
     *
     * @param location      AST node location.
     * @param typeReference Reference to the instantiated type definition.
     * @param typeArguments Arguments for the type instantiation.
     */
    public ParameterizedTypeInstantiation (AstLocation location, TypeReference typeReference,
            List<Expression> typeArguments)
    {
        super(location, typeReference);

        this.typeArguments = typeArguments;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);
        for (Expression typeArgument : typeArguments)
            typeArgument.accept(visitor);
    }

    @Override
    public CompoundType getBaseType()
    {
        return (CompoundType)super.getBaseType();
    }

    /**
     * Gets a list of parameters used in this type instantiation.
     *
     * This returns a list of parameters of the base type together with the expressions used in the
     * instantiation.
     *
     * The order of the items in list is the order of the parameters.
     *
     * @return The list of instantiated parameters.
     */
    public List<InstantiatedParameter> getInstantiatedParameters()
    {
        return Collections.unmodifiableList(instantiatedParameters);
    }

    /**
     * Class describing one parameter in a type instantiation.
     */
    public static class InstantiatedParameter
    {
        /**
         * Constructor from argument expression and parameter.
         *
         * @param argumentExpression Argument expression used for parameter instantiation.
         * @param parameter          The parameter as used in the definition of the parameterized compound type.
         */
        public InstantiatedParameter(Expression argumentExpression, Parameter parameter)
        {
            this.argumentExpression = argumentExpression;
            this.parameter = parameter;
        }

        /**
         * Gets the argument expression.
         *
         * @return The expression used for parameter instantiation.
         */
        public Expression getArgumentExpression()
        {
            return argumentExpression;
        }

        /**
         * Gets the parameter as used in the definition of the parameterized compound type.
         *
         * @return The parameter that is instantiated.
         */
        public Parameter getParameter()
        {
            return parameter;
        }


        private final Expression argumentExpression;
        private final Parameter parameter;
    }

    @Override
    ParameterizedTypeInstantiation instantiateImpl(List<TemplateParameter> templateParameters,
            List<TemplateArgument> templateArguments, TypeReference instantiatedTypeReference)
    {
        final List<Expression> instantiatedTypeArguments = new ArrayList<Expression>();
        for (Expression expression : typeArguments)
            instantiatedTypeArguments.add(expression.instantiate(templateParameters, templateArguments));

        return new ParameterizedTypeInstantiation(getLocation(), instantiatedTypeReference,
                instantiatedTypeArguments);
    }

    @Override
    void resolve()
    {
        // check if referenced type is a parameterized compound type
        final ZserioType baseType = super.getBaseType();
        final boolean isParameterized =
                baseType instanceof CompoundType && !((CompoundType)baseType).getTypeParameters().isEmpty();
        if (isParameterized)
        {
            final CompoundType compoundType = getBaseType();
            final List<Parameter> typeParameters = compoundType.getTypeParameters();
            // verify that number of arguments corresponds with the type definition
            if (typeArguments.size() != typeParameters.size())
            {
                throw new ParserException(getTypeReference(), "Parameterized type instantiation '" +
                        ZserioTypeUtil.getReferencedFullName(getTypeReference()) +
                        "' has wrong number of arguments! " +
                        "Expecting " + typeArguments.size() + ", got " + typeParameters.size() + "!");
            }
        }
        else
        {
            throw new ParserException(getTypeReference(), "Referenced type '" +
                    ZserioTypeUtil.getReferencedFullName(getTypeReference()) +
                    "' is not a parameterized type!");
        }
    }

    @Override
    void evaluate()
    {
        if (!isEvaluated)
        {
         // fill instantiated parameter list
            final CompoundType compoundType = getBaseType();
            final List<Parameter> typeParameters = compoundType.getTypeParameters();
            for (int i = 0; i < typeParameters.size(); i++)
            {
                instantiatedParameters.add(new InstantiatedParameter(
                        typeArguments.get(i), typeParameters.get(i)));
            }
        }
        isEvaluated = true;
    }

    @Override
    void check()
    {
        // check all argument types in instantiated parameter list
        for (InstantiatedParameter instantiatedParameter : instantiatedParameters)
        {
            final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
            if (!argumentExpression.isExplicitVariable())
            {
                final TypeReference parameterTypeReference =
                        instantiatedParameter.getParameter().getTypeReference();
                ExpressionUtil.checkExpressionType(argumentExpression, parameterTypeReference);
            }
        }
    }

    private final List<Expression> typeArguments;
    private final List<InstantiatedParameter> instantiatedParameters = new ArrayList<InstantiatedParameter>();

    private boolean isEvaluated = false;
}
