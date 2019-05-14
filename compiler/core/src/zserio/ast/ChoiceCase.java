package zserio.ast;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * AST node for cases defined by choice types.
 */
public class ChoiceCase extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param token           ANTLR4 token to localize AST node in the sources.
     * @param caseExpressions List of all case expressions associated to this choice case.
     * @param caseField       Case field associated to this choice case or null if it's not defined.
     */
    public ChoiceCase(Token token, List<ChoiceCaseExpression> caseExpressions, Field caseField)
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
        for (ChoiceCaseExpression caseExpression : caseExpressions)
            caseExpression.accept(visitor);
        if (caseField != null)
            caseField.accept(visitor);
    }

    /**
     * Gets expressions defined by the choice case.
     *
     * @return Choice case expression list.
     */
    public List<ChoiceCaseExpression> getExpressions()
    {
        return Collections.unmodifiableList(caseExpressions);
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

    private final List<ChoiceCaseExpression> caseExpressions;
    private final Field caseField;
};
