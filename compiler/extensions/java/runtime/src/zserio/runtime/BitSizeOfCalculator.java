package zserio.runtime;

import java.io.IOException;

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
     *
     * @throws ZserioError Throws if given value is out of range for varint16 type.
     */
    public static int getBitSizeOfVarInt16(short value) throws ZserioError
    {
        final short absoluteValue = (short) Math.abs(value);
        if (absoluteValue >= (short)(1 << (6 + 8)))
            throw new ZserioError("getBitSizeOfVarInt16: Value " + value + " is out of range for " +
                    "VarInt16.");

        return (absoluteValue < (short) 1 << 6) ? 8 : 16;
    }

    /**
     * Gets the bit size of varint32 value which is stored in bit stream.
     *
     * @param value varint32 value for calculation.
     *
     * @return Length of varint32 value in bits.
     *
     * @throws ZserioError Throws if given value is out of range for varint32 type.
     */
    public static int getBitSizeOfVarInt32(int value) throws ZserioError
    {
        final int absoluteValue = Math.abs(value);
        if (absoluteValue >= (1 << (6 + 7 + 7 + 8)))
            throw new ZserioError("getBitSizeOfVarInt32: Value " + value + " is out of range for " +
                    "VarInt32.");

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
     *
     * @throws ZserioError Throws if given value is out of range for varint64 type.
     */
    public static int getBitSizeOfVarInt64(long value) throws ZserioError
    {
        final long absoluteValue = Math.abs(value);
        if (absoluteValue >= (1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)))
            throw new ZserioError("getBitSizeOfVarInt64: Value " + value + " is out of range for " +
                    "VarInt64.");

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
        else if (absoluteValue < (1L << 47))
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
     *
     * @throws ZserioError Throws if given value is out of range for varuint16 type.
     */
    public static int getBitSizeOfVarUInt16(short value) throws ZserioError
    {
        if (value < 0)
            throw new ZserioError("getBitSizeOfVarUInt16: Value " + value + " is out of range for " +
                    "VarUInt16.");

        return (value < (1 << 7)) ? 8 : 16;
    }

    /**
     * Gets the bit size of varuint32 value which is stored in bit stream.
     *
     * @param value varuint32 value for calculation.
     *
     * @return Length of varuint32 value in bits.
     *
     * @throws ZserioError Throws if given value is out of range for varuint32 type.
     */
    public static int getBitSizeOfVarUInt32(int value) throws ZserioError
    {
        if (value < 0 || value >= (1 << (7 + 7 + 7 + 8)))
            throw new ZserioError("getBitSizeOfVarUInt32: Value " + value + " is out of range for " +
                    "VarUInt32.");

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
     *
     * @throws ZserioError Throws if given value is out of range for varuint64 type.
     */
    public static int getBitSizeOfVarUInt64(long value) throws ZserioError
    {
        if (value < 0 || value >= (1L << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)))
            throw new ZserioError("getBitSizeOfVarUInt64: Value " + value + " is out of range for " +
                    "VarUInt64.");

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
        else if (value < (1L << 48))
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
     * Gets the bit size of Zserio string value which is stored in bit stream.
     *
     * @param value Zserio string value for calculation.
     *
     * @return Length of Zserio string value in bits.
     *
     * @throws ZserioError Throws if given string is too long for string type.
     */
    public static int getBitSizeOfString(String value) throws ZserioError
    {
        final long stringBytes = sizeOfString(value);

        // the string consists of varuint64 for size followed by the UTF-8 encoded string
        return getBitSizeOfVarUInt64(stringBytes) + (int)BitPositionUtil.bytesToBits(stringBytes);
    }

    private static long sizeOfString(final String str)
    {
        long size = 0;
        try
        {
            if (str != null)
            {
                size = str.getBytes("UTF-8").length;
            }
        }
        catch (IOException e)
        {}

        return size;
    }
}
