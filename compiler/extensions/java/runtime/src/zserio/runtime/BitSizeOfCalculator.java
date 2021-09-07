package zserio.runtime;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.io.BitBuffer;

/**
 * The class provides common methods to calculate bit size of an variable stored in the bit stream.
 */
public class BitSizeOfCalculator
{
    /**
     * Gets the bit size of varint16 value which is stored in bit stream.
     *
     * @param value varint16 value for calculation.
     *
     * @return Length of varint16 value in bits.
     */
    public static int getBitSizeOfVarInt16(short value)
    {
        final short absoluteValue = (short) Math.abs(value);
        if (absoluteValue >= (short)(1 << (6 + 8)))
            throw new ZserioError("BitSizeOfCalculator: Value '" + value + "' is out of range for varint16!");

        return (absoluteValue < (short) 1 << 6) ? 8 : 16;
    }

    /**
     * Gets the bit size of varint32 value which is stored in bit stream.
     *
     * @param value varint32 value for calculation.
     *
     * @return Length of varint32 value in bits.
     */
    public static int getBitSizeOfVarInt32(int value)
    {
        final int absoluteValue = Math.abs(value);
        if (absoluteValue >= (1 << (6 + 7 + 7 + 8)))
            throw new ZserioError("BitSizeOfCalculator: Value '" + value + "' is out of range for varint32!");

        int bitSize = 0;
        if (absoluteValue < 1 << 6)
        {
            bitSize = 8;
        }
        else if (absoluteValue < 1 << 13)
        {
            bitSize = 16;
        }
        else if (absoluteValue < 1 << 20)
        {
            bitSize = 24;
        }
        else
        {
            bitSize = 32;
        }

        return bitSize;
    }

    /**
     * Gets the bit size of varint64 value which is stored in bit stream.
     *
     * @param value varint64 value for calculation.
     *
     * @return Length of varint64 value in bits.
     */
    public static int getBitSizeOfVarInt64(long value)
    {
        final long absoluteValue = Math.abs(value);
        if (absoluteValue >= (1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)))
            throw new ZserioError("BitSizeOfCalculator: Value '" + value + "' is out of range for varint64!");

        int bitSize = 0;
        if (absoluteValue < (1L << 6))
        {
            bitSize = 8;
        }
        else if (absoluteValue < (1L << 13))
        {
            bitSize = 16;
        }
        else if (absoluteValue < (1L << 20))
        {
            bitSize = 24;
        }
        else if (absoluteValue < (1L << 27))
        {
            bitSize = 32;
        }
        else if (absoluteValue < (1L << 34))
        {
            bitSize = 40;
        }
        else if (absoluteValue < (1L << 41))
        {
            bitSize = 48;
        }
        else if (absoluteValue < (1L << 48))
        {
            bitSize = 56;
        }
        else
        {
            bitSize = 64;
        }

        return bitSize;
    }

    /**
     * Gets the bit size of varuint16 value which is stored in bit stream.
     *
     * @param value varuint16 value for calculation.
     *
     * @return Length of varuint16 value in bits.
     */
    public static int getBitSizeOfVarUInt16(short value)
    {
        if (value < 0)
            throw new ZserioError("BitSizeOfCalculator: Value '" + value + "' is out of range for varuint16!");

        return (value < (1 << 7)) ? 8 : 16;
    }

    /**
     * Gets the bit size of varuint32 value which is stored in bit stream.
     *
     * @param value varuint32 value for calculation.
     *
     * @return Length of varuint32 value in bits.
     */
    public static int getBitSizeOfVarUInt32(int value)
    {
        if (value < 0 || value >= (1 << (7 + 7 + 7 + 8)))
            throw new ZserioError("BitSizeOfCalculator: Value '" + value + "' is out of range for varuint32!");

        int bitSize = 0;
        if (value < (1 << 7))
        {
            bitSize = 8;
        }
        else if (value < (1 << 14))
        {
            bitSize = 16;
        }
        else if (value < (1 << 21))
        {
            bitSize = 24;
        }
        else
        {
            bitSize = 32;
        }

        return bitSize;
    }

    /**
     * Gets the bit size of varuint64 value which is stored in bit stream.
     *
     * @param value varuint64 value for calculation.
     *
     * @return Length of varuint64 value in bits.
     */
    public static int getBitSizeOfVarUInt64(long value)
    {
        if (value < 0 || value >= (1L << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)))
            throw new ZserioError("BitSizeOfCalculator: Value '" + value + "' is out of range for varuint64!");

        int bitSize = 0;
        if (value < (1L << 7))
        {
            bitSize = 8;
        }
        else if (value < (1L << 14))
        {
            bitSize = 16;
        }
        else if (value < (1L << 21))
        {
            bitSize = 24;
        }
        else if (value < (1L << 28))
        {
            bitSize = 32;
        }
        else if (value < (1L << 35))
        {
            bitSize = 40;
        }
        else if (value < (1L << 42))
        {
            bitSize = 48;
        }
        else if (value < (1L << 49))
        {
            bitSize = 56;
        }
        else
        {
            bitSize = 64;
        }

        return bitSize;
    }

    /**
     * Gets the bit size of varint value which is stored in bit stream.
     *
     * @param value varint value for calculation.
     *
     * @return Length of varint value in bits.
     */
    public static int getBitSizeOfVarInt(long value)
    {
        if (value == Long.MIN_VALUE)
            return 8; // Long.MIN_VALUE is stored as -0

        long absoluteValue = Math.abs(value);

        int bitSize = 0;
        if (absoluteValue < (1L << 6))
        {
            bitSize = 8;
        }
        else if (absoluteValue < (1L << 13))
        {
            bitSize = 16;
        }
        else if (absoluteValue < (1L << 20))
        {
            bitSize = 24;
        }
        else if (absoluteValue < (1L << 27))
        {
            bitSize = 32;
        }
        else if (absoluteValue < (1L << 34))
        {
            bitSize = 40;
        }
        else if (absoluteValue < (1L << 41))
        {
            bitSize = 48;
        }
        else if (absoluteValue < (1L << 48))
        {
            bitSize = 56;
        }
        else if (absoluteValue < (1L << 55))
        {
            bitSize = 64;
        }
        else
        {
            bitSize = 72;
        }

        return bitSize;
    }

    /**
     * Gets the bit size of varuint value which is stored in bit stream.
     *
     * @param value varuint value for calculation.
     *
     * @return Length of varuint value in bits.
     */
    public static int getBitSizeOfVarUInt(BigInteger value)
    {
        if (value.compareTo(BigInteger.ZERO) == -1 || value.compareTo(VARUINT_MAX) == 1)
            throw new ZserioError("BitSizeOfCalculator: Value '" + value + "' is out of range for varuint!");

        int bitSize = 0;
        if (value.compareTo(BigInteger.valueOf(1L << 7)) == -1)
        {
            bitSize = 8;
        }
        else if (value.compareTo(BigInteger.valueOf(1L << 14)) == -1)
        {
            bitSize = 16;
        }
        else if (value.compareTo(BigInteger.valueOf(1L << 21)) == -1)
        {
            bitSize = 24;
        }
        else if (value.compareTo(BigInteger.valueOf(1L << 28)) == -1)
        {
            bitSize = 32;
        }
        else if (value.compareTo(BigInteger.valueOf(1L << 35)) == -1)
        {
            bitSize = 40;
        }
        else if (value.compareTo(BigInteger.valueOf(1L << 42)) == -1)
        {
            bitSize = 48;
        }
        else if (value.compareTo(BigInteger.valueOf(1L << 49)) == -1)
        {
            bitSize = 56;
        }
        else if (value.compareTo(BigInteger.valueOf(1L << 56)) == -1)
        {
            bitSize = 64;
        }
        else
        {
            bitSize = 72;
        }

        return bitSize;
    }

    /**
     * Gets the bit size of varsize value which is stored in bit stream.
     *
     * @param value varsize value for calculation.
     *
     * @return Length of varsize value in bits.
     */
    public static int getBitSizeOfVarSize(int value)
    {
        if (value < 0)
            throw new ZserioError("BitSizeOfCalculator: Value '" + value + "' is out of range for varsize!");

        int bitSize = 0;
        if (value < (1 << 7))
        {
            bitSize = 8;
        }
        else if (value < (1 << 14))
        {
            bitSize = 16;
        }
        else if (value < (1 << 21))
        {
            bitSize = 24;
        }
        else if (value < (1 << 28))
        {
            bitSize = 32;
        }
        else
        {
            bitSize = 40;
        }

        return bitSize;
    }

    /**
     * Gets the bit size of Zserio string value which is stored in bit stream.
     *
     * @param value Zserio string value for calculation.
     *
     * @return Length of Zserio string value in bits.
     */
    public static int getBitSizeOfString(String value)
    {
        final int stringBytes = sizeOfString(value);

        // the string consists of varsize for size followed by the UTF-8 encoded string
        return getBitSizeOfVarSize(stringBytes) + (int)BitPositionUtil.bytesToBits(stringBytes);
    }

    /**
     * Gets the bit size of bit buffer which is stored in bit stream.
     *
     * @param bitBuffer Bit buffer for calculation.
     *
     * @return Length of bit buffer in bits.
     */
    public static int getBitSizeOfBitBuffer(BitBuffer bitBuffer)
    {
        final long bitBufferSize = bitBuffer.getBitSize();

        // bit buffer consists of varsize for bit size followed by the bits
        return getBitSizeOfVarSize(VarSizeUtil.convertBitBufferSizeToInt(bitBufferSize)) + (int)bitBufferSize;
    }

    private static int sizeOfString(final String str)
    {
        int size = 0;
        try
        {
            size = str.getBytes("UTF-8").length;
        }
        catch (IOException e)
        {
        }

        return size;
    }

    private static final BigInteger VARUINT_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
}
