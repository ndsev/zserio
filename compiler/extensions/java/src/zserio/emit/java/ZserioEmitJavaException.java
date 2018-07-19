package zserio.emit.java;

import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;

/**
 * The exception which can be thrown by Java emitter.
 */
public class ZserioEmitJavaException extends ZserioEmitException
{
    /**
     * Constructor.
     *
     * @param message Exception message.
     */
    public ZserioEmitJavaException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new ZserioEmitJavaException indicating that the emit-operation is not supported in case
     * of the specified type.
     *
     * @param type Type which is not supported.
     */
    public ZserioEmitJavaException(ZserioType type)
    {
        super(type);
    }

    private static final long serialVersionUID = 1L;
}
