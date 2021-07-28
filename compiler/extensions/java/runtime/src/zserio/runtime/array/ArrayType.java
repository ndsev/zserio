package zserio.runtime.array;

/**
 * Enumeration for all available zserio array types.
 */
public enum ArrayType
{
    /**
     * Normal zserio array with known length defined by expression.
     */
    NORMAL,

    /**
     * Auto zserio array with length stored as varsize in the bit stream before the array.
     */
    AUTO,

    /**
     * Implicit zserio array with length defined by the size of the rest bit stream to read.
     */
    IMPLICIT
}
