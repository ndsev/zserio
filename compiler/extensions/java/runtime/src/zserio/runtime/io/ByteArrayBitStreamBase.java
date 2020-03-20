package zserio.runtime.io;

import java.io.IOException;

/**
 * Common abstract class for bit stream reader and writer.
 */
abstract class ByteArrayBitStreamBase
{
    public long getBitPosition()
    {
        return 8 * bytePosition + bitOffset;
    }

    public int getBytePosition()
    {
        return bytePosition;
    }

    public void setBitPosition(final long bitPosition) throws IOException
    {
        bitOffset = (int)(bitPosition % 8);
        bytePosition = (int)(bitPosition / 8);
    }

    public int getBitOffset()
    {
        return bitOffset;
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
        if (numBits <= 0 || numBits > 64)
            throw new IllegalArgumentException("ByteArrayBitStreamBase: Number of bits " + numBits +
                    " is out of range [1, 64].");
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
     * The default character set.
     */
    protected static final String DEFAULT_CHARSET_NAME = "UTF-8";

    /**
     * Byte value used for modulus calculations.
     */
    protected static final byte BYTE_MOD_MASK = 0x7;
}
