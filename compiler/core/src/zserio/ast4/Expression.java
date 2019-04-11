package zserio.ast4;

import org.antlr.v4.runtime.Token;

public class Expression extends AstNodeBase
{
    public Expression(Token operatorToken)
    {
        this(operatorToken, operatorToken, false, null, null, null);
    }

    public Expression(Token locationToken, Token operatorToken, boolean isExplicit)
    {
        this(locationToken, operatorToken, true, null, null, null);
    }

    public Expression(Token locationToken, Token operatorToken, Expression operand1)
    {
        this(locationToken, operatorToken, false, operand1, null, null);
    }

    public Expression(Token locationToken, Token operatorToken, Expression operand1, Expression operand2)
    {
        this(locationToken, operatorToken, false, operand1, operand2, null);
    }

    public Expression(Token locationToken, Token operatorToken, Expression operand1, Expression operand2,
            Expression operand3)
    {
        this(locationToken, operatorToken, false, operand1, operand2, operand3);
    }

    @Override
    public void walk(ZserioListener listener)
    {
        listener.enterExpression(this);
    }

    public String getExpressionString()
    {
        return operatorToken.getText();
    }

    private Expression(Token locationToken, Token operatorToken, boolean isExplicit, Expression operand1,
            Expression operand2, Expression operand3)
    {
        super(locationToken);

        this.operatorToken = operatorToken;
        this.isExplicit = isExplicit;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;
    }

    private final Token operatorToken;
    private final boolean isExplicit;
    private final Expression operand1;
    private final Expression operand2;
    private final Expression operand3;
}
