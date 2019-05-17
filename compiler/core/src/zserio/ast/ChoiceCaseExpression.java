package zserio.ast;

import org.antlr.v4.runtime.Token;

/**
 * Choice case expression which can have its own documentation comment.
 */
public class ChoiceCaseExpression extends AstNodeWithDoc
{
    /**
     * Constructor.
     *
     * @param token ANTLR4 token to localize AST node in the sources.
     * @param expression Case expression.
     * @param docComment Documentation comment belonging to the case expression.
     */
    public ChoiceCaseExpression(Token token, Expression expression, DocComment docComment)
    {
        super(token, docComment);

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
        expression.accept(visitor);

        super.visitChildren(visitor);
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

    private final Expression expression;
}