package zserio.ast4;

import org.antlr.v4.runtime.Token;

public class Expression extends AstNodeBase
{
    public Expression(Token token, String expressionString, boolean isExplicit)
    {
        super(token);

        this.expressionString = expressionString;
        this.isExplicit = isExplicit;
    }

    @Override
    public void walk(ZserioListener listener)
    {
        listener.enterExpression(this);
    }

    public String getExpressionString()
    {
        return expressionString;
    }

    private final String expressionString;
    private final boolean isExplicit;
}