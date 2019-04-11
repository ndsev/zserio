package zserio.ast4;

import org.antlr.v4.runtime.Token;

public class Expression extends AstNodeBase
{
    public Expression(Token expressionToken)
    {
        this(expressionToken, expressionToken, false, null, null, null);
    }

    public Expression(Token locationToken, Token expressionToken, boolean isExplicit)
    {
        this(locationToken, expressionToken, true, null, null, null);
    }

    public Expression(Token locationToken, Token expressionToken, Expression operand1)
    {
        this(locationToken, expressionToken, false, operand1, null, null);
    }

    public Expression(Token locationToken, Token expressionToken, Expression operand1, Expression operand2)
    {
        this(locationToken, expressionToken, false, operand1, operand2, null);
    }

    public Expression(Token locationToken, Token expressionToken, Expression operand1, Expression operand2,
            Expression operand3)
    {
        this(locationToken, expressionToken, false, operand1, operand2, operand3);
    }

    @Override
    public void walk(ZserioListener listener)
    {
        listener.beginExpression(this);

        if (operand1 != null)
            operand1.walk(listener);
        if (operand2 != null)
            operand2.walk(listener);
        if (operand3 != null)
            operand3.walk(listener);

        listener.endExpression(this);
    }

    public String getExpressionString()
    {
        return expressionToken.getText();
    }

    private Expression(Token locationToken, Token expressionToken, boolean isExplicit, Expression operand1,
            Expression operand2, Expression operand3)
    {
        super(locationToken);

        this.expressionToken = expressionToken;
        this.isExplicit = isExplicit;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;
    }

    private final Token expressionToken;
    private final boolean isExplicit;
    private final Expression operand1;
    private final Expression operand2;
    private final Expression operand3;
}
