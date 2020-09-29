package zserio.ast;

import java.util.List;

/**
 * Choice case expression which can have its own documentation comment.
 */
public class ChoiceCaseExpression extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location    AST node location.
     * @param expression  Case expression.
     * @param docComments List of documentation comments belonging to the case expression.
     */
    public ChoiceCaseExpression(AstLocation location, Expression expression, List<DocComment> docComments)
    {
        super(location, docComments);

        this.expression = expression;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitChoiceCaseExpression(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        expression.accept(visitor);
    }

    /**
     * Gets case expression.
     *
     * @return Case expression.
     */
    public Expression getExpression()
    {
        return expression;
    }

    /**
     * Instantiate the choice case expression.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments Template arguments.
     *
     * @return New choice case expression instantiated from this using the given template arguments.
     */
    ChoiceCaseExpression instantiate(List<TemplateParameter> templateParameters,
            List<TemplateArgument> templateArguments)
    {
        final Expression instantiatedExpression =
                getExpression().instantiate(templateParameters, templateArguments);

        return new ChoiceCaseExpression(getLocation(), instantiatedExpression, getDocComments());
    }

    private final Expression expression;
}
