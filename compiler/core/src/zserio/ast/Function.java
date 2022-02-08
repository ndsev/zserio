package zserio.ast;

import java.util.List;
import java.util.Set;

import zserio.tools.ZserioToolPrinter;

/**
 * AST node for Function types.
 *
 * Function types are Zserio types as well.
 */
public class Function extends DocumentableAstNode implements ScopeSymbol
{
    /**
     * Constructor.
     *
     * @param location            AST node location.
     * @param returnTypeReference Type reference to the function return type.
     * @param name                Name of the function type.
     * @param resultExpression    Result expression of the function type.
     * @param docComments         List of documentation comments belonging to this node.
     */
    public Function(AstLocation location, TypeReference returnTypeReference, String name,
            Expression resultExpression, List<DocComment> docComments)
    {
        super(location, docComments);

        this.returnTypeReference = returnTypeReference;
        this.name = name;
        this.resultExpression = resultExpression;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitFunction(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        returnTypeReference.accept(visitor);
        resultExpression.accept(visitor);
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Gets reference to the function's return type.
     *
     * @return Type reference.
     */
    public TypeReference getReturnTypeReference()
    {
        return returnTypeReference;
    }

    /**
     * Gets expression which represents function result.
     *
     * @return Function result expression.
     */
    public Expression getResultExpression()
    {
        return resultExpression;
    }

    /**
     * Checks the function type.
     */
    void check()
    {
        // check result expression type
        ExpressionUtil.checkExpressionType(resultExpression, returnTypeReference);

        // check usage of optional fields (this is considered as a warning)
        checkOptionalReferencesInFunction();
    }

    /**
     * Instantiate the function type.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments Template arguments.
     *
     * @return New function type instantiated from this using the given template arguments.
     */
    Function instantiate(List<TemplateParameter> templateParameters, List<TemplateArgument> templateArguments)
    {
        final TypeReference instantiatedReturnTypeReference =
                returnTypeReference.instantiate(templateParameters, templateArguments);

        final Expression instantiatedResultExpression =
                resultExpression.instantiate(templateParameters, templateArguments);

        return new Function(getLocation(), instantiatedReturnTypeReference, name,
                instantiatedResultExpression, getDocComments());
    }

    private void checkOptionalReferencesInFunction()
    {
        // in case of ternary operator, we are not able to check correctness => such warning should be
        // enabled explicitly by command line
        if (!resultExpression.containsTernaryOperator())
        {
            final Set<Field> referencedFields = resultExpression.getReferencedOptionalFields().keySet();
            for (Field referencedField : referencedFields)
            {
                ZserioToolPrinter.printWarning(resultExpression, "Function '" + name + "' contains " +
                        "reference to optional field '" + referencedField.getName() + "'.");
            }
        }
    }

    private final TypeReference returnTypeReference;
    private final String name;
    private final Expression resultExpression;
}
