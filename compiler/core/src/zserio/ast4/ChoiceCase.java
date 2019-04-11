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
    public void walk(ZserioListener listener)
    {
        listener.beginChoiceCase(this);

        for (Expression caseExpression : caseExpressions)
            caseExpression.walk(listener);
        if (caseField != null)
            caseField.walk(listener);

        listener.endChoiceCase(this);
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

    /*public static class CaseExpression implements Serializable
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

        private static final long serialVersionUID = 703521218397552727L;

        private final Expression        expression;
        private final DocCommentToken   docComment;
    }*/

    /*@Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        if (child instanceof Expression)
        {
            if (lastCaseToken == null)
                lastCaseToken = this;
            lastCaseToken.evaluateHiddenDocComment(choiceType);
            caseExpressions.add(new CaseExpression((Expression)child, lastCaseToken.getHiddenDocComment()));
        }
        else
        {
            switch (child.getType())
            {
            case ZserioParserTokenTypes.CASE:
                if (!(child instanceof ChoiceCase))
                    return false;
                lastCaseToken = (ChoiceCase)child;
                break;

            case ZserioParserTokenTypes.FIELD:
                if (!(child instanceof Field))
                    return false;
                caseField = (Field)child;
                choiceType.addField(caseField);
                break;

            default:
                return false;
            }
        }

        return true;
    }*/

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
