package zserio.emit.cpp;

import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;

/**
 * The exception which can be thrown by C++ emitter.
 */
public class ZserioEmitCppException extends ZserioEmitException
{
    /**
     * Constructor.
     *
     * @param message Exception message.
     */
    public ZserioEmitCppException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new ZserioEmitJavaException indicating that the emit-operation is not supported in case
     * of the specified type.
     *
     * @param type Type which is not supported.
     */
    public ZserioEmitCppException(ZserioType type)
    {
        super(type);
    }

    private static final long serialVersionUID = 1L;
}
