package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zserio.antlr.util.ParserException;

/**
 * AST node for Parameterized Type Instantiations.
 *
 * Parameterized Type Instantiations are type references to parameterized types with given arguments. All others
 * type references are modeled by AST node TypeReference.
 *
 * Parameterized type instantiations are Zserio types as well.
 */
public class TypeInstantiation extends AstNodeBase implements ZserioType
{
    /**
     * Constructor.
     *
     * @param location       AST node location.
     * @param referencedType Zserio type of the parameterized type.
     * @param arguments      List of all arguments which belong to the parameterized type.
     */
    TypeInstantiation(AstLocation location, TypeReference referencedType, List<Expression> arguments)
    {
        super(location);

        this.referencedType = referencedType;
        this.arguments = arguments;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitTypeInstantiation(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        referencedType.accept(visitor);
        for (Expression argument : arguments)
            argument.accept(visitor);
    }

    @Override
    public Package getPackage()
    {
        return baseType.getPackage();
    }

    @Override
    public String getName()
    {
        return referencedType.getName() + "()";
    }

    /**
     * Gets the type to which this type instantiation refers.
     *
     * @return Type referenced by this instantiation.
     */
    public ZserioType getReferencedType()
    {
        return referencedType;
    }

    /**
     * Gets the compound type to which this type instantiation refers.
     *
     * @return Base type of this type instantiation.
     */
    public CompoundType getBaseType()
    {
        return baseType;
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

    /**
     * Evaluates base type of this type instantiation.
     *
     * This method can be called from Expression.evaluate() method if some expression refers to the type
     * instantiation before its definition. Therefore 'isEvaluated' check is necessary.
     */
    void evaluate()
    {
        if (!isEvaluated)
        {
            // check if referenced type is a compound type
            final ZserioType resolvedReferencedType = TypeReference.resolveBaseType(referencedType);
            if (!(resolvedReferencedType instanceof CompoundType))
                throw new ParserException(referencedType, "Parameterized type instantiation '" + getName() +
                        "' does not refer to a compound type!");
            baseType = (CompoundType)resolvedReferencedType;

            // check if referenced type is a parameterized type
            final List<Parameter> parameters = baseType.getTypeParameters();
            final int numParameters = parameters.size();
            if (numParameters == 0)
                throw new ParserException(referencedType, "Parameterized type instantiation '" + getName() +
                        "' does not refer to a parameterized type!");
            if (arguments.size() != numParameters)
                throw new ParserException(referencedType, "Parameterized type instantiation '" + getName() +
                        "' has wrong number of arguments (" + arguments.size() + " != " + numParameters + ")!");

            // fill instantiated parameter list
            for (int i = 0; i < numParameters; i++)
                instantiatedParameters.add(new InstantiatedParameter(arguments.get(i), parameters.get(i)));

            isEvaluated = true;
        }
    }

    /**
     * Checks base type of this type instantiation.
     */
    void check()
    {
        // check all argument types in instantiated parameter list
        for (InstantiatedParameter instantiatedParameter : instantiatedParameters)
        {
            final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
            if (!argumentExpression.isExplicitVariable())
            {
                ExpressionUtil.checkExpressionType(argumentExpression, TypeReference.resolveBaseType(
                        instantiatedParameter.getParameter().getParameterType()));
            }
        }
    }

    /**
     * Instantiate the parameterized type instantiation.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments Template arguments.
     *
     * @return New parameterized type instantiation instantiated from this using the given template arguments.
     */
    TypeInstantiation instantiate(List<TemplateParameter> templateParameters,
            List<ZserioType> templateArguments)
    {
        final ZserioType instantiatedReferencedType =
                referencedType.instantiate(templateParameters, templateArguments);
        // TODO[Mi-L@]: How to postpone the error to the resolve phase?
        //              Can we make a type reference for built-in types?
        if (!(instantiatedReferencedType instanceof TypeReference))
            throw new ParserException(instantiatedReferencedType, instantiatedReferencedType.getName() +
                    " cannot be used as a parameterized type!");

        final List<Expression> instantiatedArguments = new ArrayList<Expression>();
        for (Expression expression : arguments)
            instantiatedArguments.add(expression.instantiate(templateParameters, templateArguments));

        return new TypeInstantiation(getLocation(), (TypeReference)instantiatedReferencedType,
                instantiatedArguments);
    }

    private final TypeReference referencedType;
    private final List<Expression> arguments;

    private boolean isEvaluated = false;
    private CompoundType baseType = null;
    private final List<InstantiatedParameter> instantiatedParameters = new ArrayList<InstantiatedParameter>();
}
