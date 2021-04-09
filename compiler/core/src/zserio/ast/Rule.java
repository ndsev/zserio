package zserio.ast;

import java.util.List;

/**
 * AST node for rule.
 */
public class Rule extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location          AST node location.
     * @param ruleIdExpression  Constant string expression defining the rule ID.
     * @param docComments       List of documentation comments belonging to this node.
     */
    public Rule(AstLocation location, Expression ruleIdExpression, List<DocComment> docComments)
    {
        super(location, docComments);

        this.ruleIdExpression = ruleIdExpression;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitRule(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        ruleIdExpression.accept(visitor);
    }

    /**
     * Gets rule ID.
     *
     * @return Rule identifier.
     */
    public String getRuleId()
    {
        return ruleId;
    }

    /**
     * Evaluates the rule.
     */
    void evaluate()
    {
        ruleId = ruleIdExpression.getStringValue();
        if (ruleId == null)
        {
            throw new ParserException(ruleIdExpression,
                    "Rule identifier must be a constant string expression!");
        }

        RuleIdValidator.validate(ruleId, ruleIdExpression.getLocation());
    }

    private final Expression ruleIdExpression;

    private String ruleId = null;
}
