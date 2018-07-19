package zserio.emit.doc;

import zserio.ast.Expression;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;

/**
 * Exception that can be thrown while emitting zserio to Html.
 */
@SuppressWarnings("serial")
public class ZserioEmitHtmlException extends ZserioEmitException
{

    /**
     * Constructs a new ZserioEmitHtmlException with null as it detail message.
     */
    public ZserioEmitHtmlException()
    {
        // super(); // Super-constructor allready is automatically called here.
    }

    /**
     * Constructs a new ZserioEmitHtmlException with the specified detail message.
     *
     * @param text detail message
     */
    public ZserioEmitHtmlException( String text )
    {
        super(text);
    }

    /**
     * Constructs a new ZserioEmitHtmlException with the specified exc and
     * a detail message of ( exc==null ? null : exc.toString() )
     * ( which typically contains the class and detail message of exc )
     *
     * @param exc
     */
    public ZserioEmitHtmlException( Throwable exc )
    {
        super(exc);
    }

    /**
     * Constructs a new ZserioEmitHtmlException indicating that the
     * emit -operation is not supported in case of the specified type.
     *
     * @param t type which is not supported
     */
    public ZserioEmitHtmlException( ZserioType t )
    {
        super(t);
    }

    /**
     * Constructs a new ZserioEmitHtmlException indicating that the
     * emit -operation handling (this type of) expression is not supported.
     *
     * @param ex expression which is not supported
     */
    public ZserioEmitHtmlException( Expression ex )
    {
        super(ex);
    }
}
