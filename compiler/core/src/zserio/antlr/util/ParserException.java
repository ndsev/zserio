package zserio.antlr.util;

import org.antlr.v4.runtime.Token;

import zserio.ast.AstLocation;
import zserio.ast.AstNode;

/**
 * Unchecked exception which is used during parsing in zserio.
 *
 * This exception is unchecked because it is used by visitors.
 */
public class ParserException extends RuntimeException
{
    /**
     * Constructor from token and message.
     *
     * @param token   Token to construct from.
     * @param message Message to construct from.
     */
    public ParserException(Token token, String message)
    {
        this(new AstLocation(token), message);
    }

    /**
     * Constructor from node and message.
     *
     * @param node    AST node to construct from.
     * @param message Message to construct from.
     */
    public ParserException(AstNode node, String message)
    {
        this(node.getLocation(), message);
    }

    /**
     * Constructor from location and message.
     *
     * @param location AST location to construct from.
     * @param message  Message to construct from.
     */
    public ParserException(AstLocation location, String message)
    {
        super(message);
        this.location = location;
    }

    /**
     * Gets the AST location.
     *
     * @return Location of the AST node stored in the exception.
     */
    public AstLocation getLocation()
    {
        return location;
    }

    private transient AstLocation location;
    private static final long serialVersionUID = -2149318704048979392L;
}
