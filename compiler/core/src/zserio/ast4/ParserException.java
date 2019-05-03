package zserio.ast4;

import org.antlr.v4.runtime.Token;

public class ParserException extends RuntimeException
{
    public ParserException(Token token, String message)
    {
        this(new AstLocation(token), message);
    }

    public ParserException(AstNode node, String message)
    {
        this(node.getLocation(), message);
    }

    public ParserException(AstLocation location, String message)
    {
        super(message);
        this.location = location;
    }

    public AstLocation getLocation()
    {
        return location;
    }

    private AstLocation location;
    private static final long serialVersionUID = -2149318704048979392L;
}
