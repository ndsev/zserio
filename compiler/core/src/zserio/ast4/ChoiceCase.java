package zserio.ast4;

import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * AST node for cases defined by choice types.
 */
public class ChoiceCase extends AstNodeBase
{
    public ChoiceCase(Token token, List<Expression> caseExpressions, Field caseField)
    {
        super(token);

        this.caseExpressions = caseExpressions;
        this.caseField = caseField;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitChoiceCase(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (Expression caseExpression : caseExpressions)
            caseExpression.accept(visitor);
        if (caseField != null)
            caseField.accept(visitor);
    }

    /**
     * Gets expressions defined by the choice case.
     *
     * @return Choice case expression list.
     */
    public List<Expression> getExpressions() // TODO: List<CaseExpression> ?
    {
        return caseExpressions;
    }

    /**
     * Gets field defined by the choice case.
     *
     * @return Field defined by the choice case or null if this case is empty.
     */
    public Field getField()
    {
        return caseField;
    }

    /*public static class CaseExpression
    {
        public CaseExpression(Expression expression, DocCommentToken docComment)
        {
            this.expression = expression;
            this.docComment = docComment;
        }

        public Expression getExpression()
        {
            return expression;
        }

        public DocCommentToken getDocComment()
        {
            return docComment;
        }

        private final Expression        expression;
        private final DocCommentToken   docComment;
    }*/ // TODO:

    /**
     * Sets the choice type which is owner of the choice case.
     *
     * @param choiceType Owner to set.
     */
    protected void setChoiceType(ChoiceType choiceType)
    {
        this.choiceType = choiceType;
    }

    private ChoiceType choiceType;
    private final List<Expression> caseExpressions;
    private final Field caseField;
    //private final ChoiceCase lastCaseToken; // TODO:
};
