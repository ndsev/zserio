package zserio.ast4;

import org.antlr.v4.runtime.Token;

public class ParserException extends RuntimeException
{
    public ParserException(Token token, String message)
    {
        this(new AstNodeLocation(token), message);
    }

    public ParserException(AstNode node, String message)
    {
        this(node.getLocation(), message);
    }

    public ParserException(AstNodeLocation location, String message)
    {
        super(message);
        this.location = location;
    }

    public AstNodeLocation getLocation()
    {
        return location;
    }

    private AstNodeLocation location;
    private static final long serialVersionUID = -2149318704048979392L;
}
