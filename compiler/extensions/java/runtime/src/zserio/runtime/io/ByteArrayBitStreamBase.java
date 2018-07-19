package zserio.runtime.io;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * Common abstract class for bit stream reader and writer.
 */
abstract class ByteArrayBitStreamBase
{
    /**
     * Constructs object from given byte ordering.
     *
     * @param byteOrder Byte order to construct from (BIG_ENDIAN or LITTLE_ENDIAN).
     */
    public ByteArrayBitStreamBase(ByteOrder byteOrder)
    {
        this.byteOrder = byteOrder;
    }

    public long getBitPosition()
    {
        return BITS_PER_BYTE * bytePosition + bitOffset;
    }

    public void setBitPosition(final long bitPosition) throws IOException
    {
        bitOffset = (int)(bitPosition % 8);
        bytePosition = (int)(bitPosition / BITS_PER_BYTE);
    }

    public int getBytePosition()
    {
        return bytePosition;
    }

    public void seek(final int bytePosition)
    {
        this.bytePosition = bytePosition;
        this.bitOffset = 0;
    }

    public int getBitOffset()
    {
        return bitOffset;
    }

    public void rewind() throws IOException
    {
        setBitPosition(0);
    }

    /**
     * Checks if the given numBits value is situated in the range 0 to 64.
     *
     * @param numBits Number of bits to check.
     *
     * @throws IllegalArgumentException If the numBits value is smaller than zero or greater than 64.
     */
    protected static void checkRange(final int numBits) throws IllegalArgumentException
    {
        if (numBits <= 0 || numBits > BITS_PER_LONG)
            throw new IllegalArgumentException("ByteArrayBitStreamBase: Number of bits " + numBits +
                    " is out of range [1, " + BITS_PER_LONG + "].");
    }

    /**
     * The current byte position.
     */
    protected int bytePosition;

    /**
     * The current bit offset.
     */
    protected int bitOffset;

    /**
     * The byte order.
     */
    protected final ByteOrder byteOrder;

    /**
     * The default byte order.
     */
    protected static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    /**
     * The default character set.
     */
    protected static final String DEFAULT_CHARSET_NAME = "UTF-8";

    /**
     * Byte value used for modulus calculations.
     */
    protected static final byte BYTE_MOD_MASK = 0x7;

    /**
     * The number of bits per byte.
     */
    protected static final int BITS_PER_BYTE = 0x8;

    /**
     * The number of bits per short.
     */
    protected static final int BITS_PER_SHORT = 0x10;

    /**
     * The number of bits per integer.
     */

    protected static final int BITS_PER_INT = 0x20;
    /**
     * The number of bits per long integer.
     */
    protected static final int BITS_PER_LONG = 0x40;
}
