package zserio.ast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.ast.doc.DocCommentToken;

/**
 * AST node for cases defined by choice types.
 */
public class ChoiceCase extends TokenAST
{
    /**
     * Default constructor.
     */
    public ChoiceCase()
    {
        caseExpressions = new ArrayList<CaseExpression>();
    }

    /**
     * Sets the choice type which is owner of the choice case.
     *
     * @param choiceType Owner to set.
     */
    public void setChoiceType(ChoiceType choiceType)
    {
        this.choiceType = choiceType;
    }

    /**
     * Gets expressions defined by the choice case.
     *
     * @return Choice case expression list.
     */
    public List<CaseExpression> getExpressions()
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

    public static class CaseExpression implements Serializable
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
    }

    @Override
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
    }

    private static final long serialVersionUID = 703521218397552828L;

    private ChoiceType                  choiceType;
    private final List<CaseExpression>  caseExpressions;
    private Field                       caseField;
    private ChoiceCase                  lastCaseToken;
};
