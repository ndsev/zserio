package zserio.emit.common;

import zserio.ast.ZserioException;
import zserio.ast.Expression;
import zserio.ast.ZserioType;

/**
 * Exception that can be thrown while emitting zserio.
 *
 * TODO This exception should be reviewed. It's too late to throw exception during emitting - only real
 * exceptions are acceptable.
 */
@SuppressWarnings("serial")
public class ZserioEmitException extends ZserioException
{

    /**
     * Constructs a new ZserioEmitException with null as it detail message.
     */
    public ZserioEmitException()
    {
        // super(); // Super-constructor allready is automatically called here.
    }

    /**
     * Constructs a new ZserioEmitException with the specified detail message.
     *
     * @param text detail message
     */
    public ZserioEmitException( String text )
    {
        super(text);
    }

    /**
     * Constructs a new ZserioEmitException with the specified exc and
     * a detail message of ( exc==null ? null : exc.toString() )
     * ( which typically contains the class and detail message of exc )
     *
     * @param exc
     */
    public ZserioEmitException( Throwable exc )
    {
        super(exc);
    }

    /**
     * Constructs a new ZserioEmitException indicating that the
     * emit -operation is not supported in case of the specified type.
     *
     * @param t type which is not supported
     */
    public ZserioEmitException( ZserioType ti )
    {
        this(
            "Unsupported operation for type: "
            + ti
            + ( ti == null ?
                "" :
                " ( getName=" + ti.getName() + " )"
              )
        );
    }


    /**
     * Constructs a new ZserioEmitException indicating that the
     * emit -operation handling (this type of) expression is not supported.
     *
     * @param ex expression which is not supported
     */
    public ZserioEmitException( Expression ex )
    {
        this(
            "Unsupported operation for expression: "
            + ex
            + ( ex == null ?
                "" :
                " ( getExprType=" + ex.getType() + " )"
              )
        );
    }

}

